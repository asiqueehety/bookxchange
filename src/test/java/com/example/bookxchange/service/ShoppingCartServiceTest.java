package com.example.bookxchange.service;

import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.CartItem;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.CartItemRepository;
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

@DisplayName("ShoppingCartService Unit Tests")
public class ShoppingCartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private User testUser;
    private Book testBook;
    private CartItem testCartItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        testUser = User.builder()
                .uid("user1")
                .username("test_user")
                .userEmail("test@test.com")
                .build();

        // Setup test book
        testBook = Book.builder()
                .bookId("book1")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(new BigDecimal("19.99"))
                .quantityLeft(10)
                .build();

        // Setup test cart item
        testCartItem = CartItem.builder()
                .cartItemId("cart1")
                .user(testUser)
                .book(testBook)
                .quantity(2)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should add book to cart successfully")
    public void testAddToCart_Success() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(cartItemRepository.findByUserAndBookAndIsActive(testUser, testBook, true))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        CartItemDTO result = shoppingCartService.addToCart("user1", "book1", 2);

        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quantity is zero")
    public void testAddToCart_ZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingCartService.addToCart("user1", "book1", 0);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quantity is negative")
    public void testAddToCart_NegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            shoppingCartService.addToCart("user1", "book1", -5);
        });
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user not found")
    public void testAddToCart_UserNotFound() {
        when(userRepository.findById("unknown_user")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.addToCart("unknown_user", "book1", 2);
        });
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book not found")
    public void testAddToCart_BookNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("unknown_book")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.addToCart("user1", "unknown_book", 2);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when insufficient stock")
    public void testAddToCart_InsufficientStock() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> {
            shoppingCartService.addToCart("user1", "book1", 20);  // More than available
        });
    }

    @Test
    @DisplayName("Should update quantity when adding existing item to cart")
    public void testAddToCart_ExistingItem() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(cartItemRepository.findByUserAndBookAndIsActive(testUser, testBook, true))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        CartItemDTO result = shoppingCartService.addToCart("user1", "book1", 3);

        assertNotNull(result);
        assertEquals(5, testCartItem.getQuantity());  // 2 + 3
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should remove book from cart successfully")
    public void testRemoveFromCart_Success() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findById("cart1")).thenReturn(Optional.of(testCartItem));

        shoppingCartService.removeFromCart("user1", "cart1");

        verify(cartItemRepository, times(1)).deleteById("cart1");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when cart item not found")
    public void testRemoveFromCart_CartItemNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findById("unknown_cart")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.removeFromCart("user1", "unknown_cart");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user not authorized")
    public void testRemoveFromCart_UnauthorizedUser() {
        User differentUser = User.builder()
                .uid("user2")
                .username("different_user")
                .build();

        when(userRepository.findById("user2")).thenReturn(Optional.of(differentUser));
        when(cartItemRepository.findById("cart1")).thenReturn(Optional.of(testCartItem));

        assertThrows(IllegalArgumentException.class, () -> {
            shoppingCartService.removeFromCart("user2", "cart1");
        });
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    public void testUpdateCartItemQuantity_Success() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findById("cart1")).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        CartItemDTO result = shoppingCartService.updateCartItemQuantity("user1", "cart1", 5);

        assertNotNull(result);
        assertEquals(5, testCartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with zero quantity")
    public void testUpdateCartItemQuantity_ZeroQuantity() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> {
            shoppingCartService.updateCartItemQuantity("user1", "cart1", 0);
        });
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user not found during update")
    public void testUpdateCartItemQuantity_UserNotFound() {
        when(userRepository.findById("unknown_user")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.updateCartItemQuantity("unknown_user", "cart1", 5);
        });
    }

    @Test
    @DisplayName("Should get user cart successfully")
    public void testGetUserCart_Success() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(testCartItem);

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findByUserAndIsActive(testUser, true)).thenReturn(cartItems);

        CartDTO result = shoppingCartService.getCart("user1");

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        verify(cartItemRepository, times(1)).findByUserAndIsActive(testUser, true);
    }

    @Test
    @DisplayName("Should return empty cart when no items")
    public void testGetUserCart_Empty() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findByUserAndIsActive(testUser, true)).thenReturn(new ArrayList<>());

        CartDTO result = shoppingCartService.getCart("user1");

        assertNotNull(result);
        assertEquals(0, result.getTotalItems());
    }

    @Test
    @DisplayName("Should clear cart successfully")
    public void testClearCart_Success() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));

        shoppingCartService.clearCart("user1");

        verify(cartItemRepository, times(1)).deleteByUserAndIsActive(testUser, true);
    }

    @Test
    @DisplayName("Should get cart item count successfully")
    public void testGetCartItemCount_Success() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(testCartItem);

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(cartItemRepository.findByUserAndIsActive(testUser, true)).thenReturn(cartItems);

        Integer result = shoppingCartService.getCartItemCount("user1");

        assertNotNull(result);
        assertEquals(2, result);  // testCartItem has quantity 2
    }
}

