package com.example.bookxchange.service;

import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.dto.PurchaseDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.SoldBook;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.CartItemRepository;
import com.example.bookxchange.repository.SoldBookRepository;
import com.example.bookxchange.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PurchaseService Unit Tests")
public class PurchaseServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private SoldBookRepository soldBookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private User testBuyer;
    private Book testBook;
    private CartItemDTO cartItemDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test buyer
        testBuyer = User.builder()
                .uid("buyer1")
                .username("buyer_user")
                .userEmail("buyer@test.com")
                .build();

        // Setup test book
        testBook = Book.builder()
                .bookId("book1")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(new BigDecimal("19.99"))
                .quantityLeft(10)
                .build();

        // Setup cart item DTO
        cartItemDTO = CartItemDTO.builder()
                .bookId("book1")
                .quantity(2)
                .subtotal(new BigDecimal("39.98"))
                .build();
    }

    @Test
    @DisplayName("Should validate purchase successfully with valid items")
    public void testValidatePurchase_Success() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));

        boolean result = purchaseService.validatePurchase(items);

        assertTrue(result);
        verify(bookRepository, times(1)).findById("book1");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when cart is empty")
    public void testValidatePurchase_EmptyCart() {
        List<CartItemDTO> items = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.validatePurchase(items);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when cart is null")
    public void testValidatePurchase_NullCart() {
        assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.validatePurchase(null);
        });
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book not found during validation")
    public void testValidatePurchase_BookNotFound() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);

        when(bookRepository.findById("book1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            purchaseService.validatePurchase(items);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when insufficient stock")
    public void testValidatePurchase_InsufficientStock() {
        CartItemDTO itemWithHighQuantity = CartItemDTO.builder()
                .bookId("book1")
                .quantity(20)  // More than available
                .subtotal(new BigDecimal("399.80"))
                .build();

        List<CartItemDTO> items = new ArrayList<>();
        items.add(itemWithHighQuantity);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.validatePurchase(items);
        });
    }

    @Test
    @DisplayName("Should process purchase successfully")
    public void testPurchaseBooks_Success() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(SoldBook.builder()
                .buyer(testBuyer)
                .book(testBook)
                .build());

        PurchaseDTO result = purchaseService.purchaseBooks("buyer1", items);

        assertNotNull(result);
        assertEquals("buyer1", result.getBuyerId());
        assertEquals("buyer_user", result.getBuyerUsername());
        assertEquals(1, result.getTotalItems());
        verify(bookRepository, times(2)).findById("book1");
        verify(soldBookRepository, times(1)).save(any(SoldBook.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found during purchase")
    public void testPurchaseBooks_BuyerNotFound() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);

        when(userRepository.findById("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            purchaseService.purchaseBooks("unknown_buyer", items);
        });
    }

    @Test
    @DisplayName("Should handle multiple items in purchase")
    public void testPurchaseBooks_MultipleItems() {
        Book book2 = Book.builder()
                .bookId("book2")
                .bookName("Test Book 2")
                .bookPrice(new BigDecimal("24.99"))
                .quantityLeft(5)
                .build();

        CartItemDTO item2 = CartItemDTO.builder()
                .bookId("book2")
                .quantity(1)
                .subtotal(new BigDecimal("24.99"))
                .build();

        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);
        items.add(item2);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(bookRepository.findById("book2")).thenReturn(Optional.of(book2));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(SoldBook.builder()
                .buyer(testBuyer)
                .book(testBook)
                .build());

        PurchaseDTO result = purchaseService.purchaseBooks("buyer1", items);

        assertNotNull(result);
        assertEquals(2, result.getTotalItems());
        verify(soldBookRepository, times(2)).save(any(SoldBook.class));
    }

    @Test
    @DisplayName("Should calculate total amount correctly")
    public void testPurchaseBooks_TotalAmountCalculation() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(SoldBook.builder()
                .buyer(testBuyer)
                .book(testBook)
                .build());

        PurchaseDTO result = purchaseService.purchaseBooks("buyer1", items);

        assertNotNull(result);
        assertEquals(new BigDecimal("39.98"), result.getTotalAmount());
    }

    @Test
    @DisplayName("Should decrease book quantity after purchase")
    public void testPurchaseBooks_QuantityDecrease() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(cartItemDTO);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(SoldBook.builder()
                .buyer(testBuyer)
                .book(testBook)
                .build());

        purchaseService.purchaseBooks("buyer1", items);

        // Verify quantity was decreased
        assertEquals(8, testBook.getQuantityLeft());
    }

    @Test
    @DisplayName("Should get purchase history for buyer successfully")
    public void testGetPurchaseHistory_Success() {
        SoldBook soldBook1 = SoldBook.builder()
                .buyer(testBuyer)
                .book(testBook)
                .build();

        List<SoldBook> purchases = new ArrayList<>();
        purchases.add(soldBook1);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(purchases);

        List<PurchaseDTO> result = purchaseService.getPurchaseHistory("buyer1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("buyer1", result.get(0).getBuyerId());
        verify(userRepository, times(1)).findById("buyer1");
        verify(soldBookRepository, times(1)).findByBuyer(testBuyer);
    }

    @Test
    @DisplayName("Should return empty purchase history when buyer has no purchases")
    public void testGetPurchaseHistory_Empty() {
        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(new ArrayList<>());

        List<PurchaseDTO> result = purchaseService.getPurchaseHistory("buyer1");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found in purchase history")
    public void testGetPurchaseHistory_BuyerNotFound() {
        when(userRepository.findById("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            purchaseService.getPurchaseHistory("unknown_buyer");
        });
    }

    @Test
    @DisplayName("Should get total purchase amount for buyer successfully")
    public void testGetTotalPurchaseAmount_Success() {
        Book book2 = Book.builder()
                .bookId("book2")
                .bookName("Book 2")
                .bookPrice(new BigDecimal("29.99"))
                .quantityLeft(5)
                .build();

        SoldBook soldBook1 = SoldBook.builder()
                .buyer(testBuyer)
                .book(testBook)
                .build();

        SoldBook soldBook2 = SoldBook.builder()
                .buyer(testBuyer)
                .book(book2)
                .build();

        List<SoldBook> purchases = new ArrayList<>();
        purchases.add(soldBook1);
        purchases.add(soldBook2);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(purchases);

        BigDecimal result = purchaseService.getTotalPurchaseAmount("buyer1");

        assertNotNull(result);
        assertEquals(new BigDecimal("49.98"), result);
        verify(userRepository, times(1)).findById("buyer1");
    }

    @Test
    @DisplayName("Should return zero total purchase amount when buyer has no purchases")
    public void testGetTotalPurchaseAmount_Zero() {
        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(new ArrayList<>());

        BigDecimal result = purchaseService.getTotalPurchaseAmount("buyer1");

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found for total purchase amount")
    public void testGetTotalPurchaseAmount_BuyerNotFound() {
        when(userRepository.findById("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            purchaseService.getTotalPurchaseAmount("unknown_buyer");
        });
    }
}

