package com.example.bookxchange.service;

import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.SoldBook;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SoldBookService Unit Tests")
public class SoldBookServiceTest {

    @Mock
    private SoldBookRepository soldBookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private SoldBookService soldBookService;

    private User testBuyer;
    private User testSeller;
    private Book testBook;
    private SoldBook testSoldBook;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test buyer
        testBuyer = User.builder()
                .uid("buyer1")
                .username("buyer_user")
                .userEmail("buyer@test.com")
                .build();

        // Setup test seller
        testSeller = User.builder()
                .uid("seller1")
                .username("seller_user")
                .userEmail("seller@test.com")
                .build();

        // Setup test book
        testBook = Book.builder()
                .bookId("book1")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(new BigDecimal("19.99"))
                .quantityLeft(10)
                .seller(testSeller)
                .build();

        // Setup test sold book
        testSoldBook = SoldBook.builder()
                .soldId("sold1")
                .buyer(testBuyer)
                .book(testBook)
                .build();
    }

    @Test
    @DisplayName("Should record sale successfully")
    public void testRecordSale_Success() {
        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(testSoldBook);

        SoldBook result = soldBookService.recordSale("buyer1", "book1");

        assertNotNull(result);
        assertEquals(testBuyer, result.getBuyer());
        assertEquals(testBook, result.getBook());
        verify(soldBookRepository, times(1)).save(any(SoldBook.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found")
    public void testRecordSale_BuyerNotFound() {
        when(userRepository.findById("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            soldBookService.recordSale("unknown_buyer", "book1");
        });
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book not found")
    public void testRecordSale_BookNotFound() {
        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(bookRepository.findById("unknown_book")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            soldBookService.recordSale("buyer1", "unknown_book");
        });
    }

    @Test
    @DisplayName("Should get buyer purchases successfully")
    public void testGetBuyerPurchases_Success() {
        List<SoldBook> purchases = new ArrayList<>();
        purchases.add(testSoldBook);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(purchases);

        List<BookDTO> result = soldBookService.getBuyerPurchases("buyer1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(soldBookRepository, times(1)).findByBuyer(testBuyer);
    }

    @Test
    @DisplayName("Should return empty list when buyer has no purchases")
    public void testGetBuyerPurchases_Empty() {
        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(new ArrayList<>());

        List<BookDTO> result = soldBookService.getBuyerPurchases("buyer1");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found during getPurchases")
    public void testGetBuyerPurchases_BuyerNotFound() {
        when(userRepository.findById("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            soldBookService.getBuyerPurchases("unknown_buyer");
        });
    }

    @Test
    @DisplayName("Should get seller sales successfully")
    public void testGetSellerSales_Success() {
        List<SoldBook> allSales = new ArrayList<>();
        allSales.add(testSoldBook);

        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(allSales);

        List<SoldBook> result = soldBookService.getSellerSales("seller1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(soldBookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when seller has no sales")
    public void testGetSellerSales_Empty() {
        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(new ArrayList<>());

        List<SoldBook> result = soldBookService.getSellerSales("seller1");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when seller not found")
    public void testGetSellerSales_SellerNotFound() {
        when(userRepository.findById("unknown_seller")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            soldBookService.getSellerSales("unknown_seller");
        });
    }

    @Test
    @DisplayName("Should filter sales by seller correctly")
    public void testGetSellerSales_FilterCorrectly() {
        User differentSeller = User.builder()
                .uid("seller2")
                .username("seller2_user")
                .build();

        Book book2 = Book.builder()
                .bookId("book2")
                .bookName("Different Book")
                .bookPrice(new BigDecimal("24.99"))
                .seller(differentSeller)
                .build();

        SoldBook soldBook2 = SoldBook.builder()
                .soldId("sold2")
                .buyer(testBuyer)
                .book(book2)
                .build();

        List<SoldBook> allSales = new ArrayList<>();
        allSales.add(testSoldBook);
        allSales.add(soldBook2);

        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(allSales);

        List<SoldBook> result = soldBookService.getSellerSales("seller1");

        assertEquals(1, result.size());
        assertEquals(testSoldBook, result.get(0));
    }

    @Test
    @DisplayName("Should get seller sales count successfully")
    public void testGetSellerSalesCount_Success() {
        List<SoldBook> sales = new ArrayList<>();
        sales.add(testSoldBook);

        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(sales);

        Integer result = soldBookService.getSellerSalesCount("seller1");

        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Should return zero sales count when seller has no sales")
    public void testGetSellerSalesCount_Zero() {
        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(new ArrayList<>());

        Integer result = soldBookService.getSellerSalesCount("seller1");

        assertEquals(0, result);
    }

    @Test
    @DisplayName("Should calculate seller revenue successfully")
    public void testGetSellerRevenue_Success() {
        List<SoldBook> sales = new ArrayList<>();
        sales.add(testSoldBook);

        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(sales);

        BigDecimal result = soldBookService.getSellerRevenue("seller1");

        assertNotNull(result);
        assertEquals(new BigDecimal("19.99"), result);
    }

    @Test
    @DisplayName("Should calculate zero revenue when seller has no sales")
    public void testGetSellerRevenue_Zero() {
        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(new ArrayList<>());

        BigDecimal result = soldBookService.getSellerRevenue("seller1");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate total revenue from multiple sales")
    public void testGetSellerRevenue_Multiple() {
        Book book2 = Book.builder()
                .bookId("book2")
                .bookName("Book 2")
                .bookPrice(new BigDecimal("24.99"))
                .seller(testSeller)
                .build();

        SoldBook soldBook2 = SoldBook.builder()
                .soldId("sold2")
                .buyer(testBuyer)
                .book(book2)
                .build();

        List<SoldBook> sales = new ArrayList<>();
        sales.add(testSoldBook);
        sales.add(soldBook2);

        when(userRepository.findById("seller1")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findAll()).thenReturn(sales);

        BigDecimal result = soldBookService.getSellerRevenue("seller1");

        assertEquals(new BigDecimal("44.98"), result);
    }

    @Test
    @DisplayName("Should get buyer purchase count successfully")
    public void testGetBuyerPurchaseCount_Success() {
        List<SoldBook> purchases = new ArrayList<>();
        purchases.add(testSoldBook);

        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(purchases);

        Integer result = soldBookService.getBuyerPurchaseCount("buyer1");

        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Should return zero purchase count when buyer has no purchases")
    public void testGetBuyerPurchaseCount_Zero() {
        when(userRepository.findById("buyer1")).thenReturn(Optional.of(testBuyer));
        when(soldBookRepository.findByBuyer(testBuyer)).thenReturn(new ArrayList<>());

        Integer result = soldBookService.getBuyerPurchaseCount("buyer1");

        assertEquals(0, result);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found during getPurchaseCount")
    public void testGetBuyerPurchaseCount_BuyerNotFound() {
        when(userRepository.findById("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            soldBookService.getBuyerPurchaseCount("unknown_buyer");
        });
    }
}

