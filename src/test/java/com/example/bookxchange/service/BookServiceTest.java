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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("BookService Unit Tests")
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SoldBookRepository soldBookRepository;

    @InjectMocks
    private BookService bookService;

    private User testSeller;
    private BookDTO bookDTO;
    private Book testBook;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test seller
        testSeller = User.builder()
                .uid("seller1")
                .username("seller_user")
                .userEmail("seller@test.com")
                .build();

        // Setup test book DTO
        bookDTO = BookDTO.builder()
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(new BigDecimal("19.99"))
                .quantityLeft(10)
                .bookCoverPic("http://example.com/cover.jpg")
                .bookShortPreview("This is a test book")
                .build();

        // Setup test book
        testBook = Book.builder()
                .bookId("book1")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(new BigDecimal("19.99"))
                .quantityLeft(10)
                .bookCoverPic("http://example.com/cover.jpg")
                .bookShortPreview("This is a test book")
                .seller(testSeller)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create a new book successfully")
    public void testCreateBook_Success() {
        when(userRepository.findByUsername("seller_user")).thenReturn(Optional.of(testSeller));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.createBook(bookDTO, "seller_user");

        assertNotNull(result);
        assertEquals("Test Book", result.getBookName());
        assertEquals("Test Author", result.getBookAuthor());
        assertEquals(new BigDecimal("19.99"), result.getBookPrice());
        assertEquals(10, result.getQuantityLeft());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when seller not found")
    public void testCreateBook_SellerNotFound() {
        when(userRepository.findByUsername("unknown_seller")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookService.createBook(bookDTO, "unknown_seller");
        });
    }

    @Test
    @DisplayName("Should update book successfully")
    public void testUpdateBook_Success() {
        BookDTO updateDTO = BookDTO.builder()
                .bookName("Updated Book")
                .bookAuthor("Updated Author")
                .bookPrice(new BigDecimal("29.99"))
                .quantityLeft(20)
                .bookCoverPic("http://example.com/new-cover.jpg")
                .bookShortPreview("Updated description")
                .build();

        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.updateBook("book1", updateDTO, "seller_user");

        assertNotNull(result);
        verify(bookRepository, times(1)).findById("book1");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent book")
    public void testUpdateBook_BookNotFound() {
        when(bookRepository.findById("non_existent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBook("non_existent", bookDTO, "seller_user");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unauthorized seller tries to update book")
    public void testUpdateBook_UnauthorizedSeller() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> {
            bookService.updateBook("book1", bookDTO, "different_seller");
        });
    }

    @Test
    @DisplayName("Should delete book successfully")
    public void testDeleteBook_Success() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(soldBookRepository.findByBook(testBook)).thenReturn(new ArrayList<>());

        bookService.deleteBook("book1", "seller_user");

        verify(bookRepository, times(1)).findById("book1");
        verify(bookRepository, times(1)).deleteById("book1");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent book")
    public void testDeleteBook_BookNotFound() {
        when(bookRepository.findById("non_existent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookService.deleteBook("non_existent", "seller_user");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unauthorized seller tries to delete book")
    public void testDeleteBook_UnauthorizedSeller() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));

        assertThrows(IllegalArgumentException.class, () -> {
            bookService.deleteBook("book1", "different_seller");
        });
    }

    @Test
    @DisplayName("Should delete book and related sold books")
    public void testDeleteBook_WithSoldBooks() {
        User buyer = User.builder()
                .uid("buyer1")
                .username("buyer_user")
                .build();

        SoldBook soldBook = SoldBook.builder()
                .soldId("sold1")
                .buyer(buyer)
                .book(testBook)
                .build();

        List<SoldBook> soldBooks = new ArrayList<>();
        soldBooks.add(soldBook);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(soldBookRepository.findByBook(testBook)).thenReturn(soldBooks);

        bookService.deleteBook("book1", "seller_user");

        verify(soldBookRepository, times(1)).deleteAll(soldBooks);
        verify(bookRepository, times(1)).deleteById("book1");
    }

    @Test
    @DisplayName("Should get book by ID successfully")
    public void testGetBookById_Success() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));

        Book result = bookService.getBookById("book1");

        assertNotNull(result);
        assertEquals("Test Book", result.getBookName());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting non-existent book")
    public void testGetBookById_NotFound() {
        when(bookRepository.findById("non_existent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookService.getBookById("non_existent");
        });
    }

    @Test
    @DisplayName("Should purchase book successfully")
    public void testPurchaseBook_Success() {
        User buyer = User.builder()
                .uid("buyer1")
                .username("buyer_user")
                .build();

        when(bookRepository.findById("book1")).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("buyer_user")).thenReturn(Optional.of(buyer));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(SoldBook.builder()
                .buyer(buyer)
                .book(testBook)
                .build());

        SoldBook result = bookService.purchaseBook("book1", "buyer_user", 2);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(soldBookRepository, times(1)).save(any(SoldBook.class));
    }

    @Test
    @DisplayName("Should convert book to DTO successfully")
    public void testConvertToDTO() {
        BookDTO result = bookService.convertToDTO(testBook);

        assertNotNull(result);
        assertEquals("Test Book", result.getBookName());
        assertEquals("Test Author", result.getBookAuthor());
    }

    @Test
    @DisplayName("Should get books by seller successfully")
    public void testGetBooksBySeller_Success() {
        List<Book> sellerBooks = new ArrayList<>();
        sellerBooks.add(testBook);

        when(bookRepository.findBySellerUsername("seller_user")).thenReturn(sellerBooks);

        List<Book> result = bookService.getBooksBySeller("seller_user");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getBookName());
        verify(bookRepository, times(1)).findBySellerUsername("seller_user");
    }

    @Test
    @DisplayName("Should return empty list when seller has no books")
    public void testGetBooksBySeller_Empty() {
        when(bookRepository.findBySellerUsername("seller_user")).thenReturn(new ArrayList<>());

        List<Book> result = bookService.getBooksBySeller("seller_user");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get all available books successfully")
    public void testGetAllAvailableBooks_Success() {
        Book book2 = Book.builder()
                .bookId("book2")
                .bookName("Another Book")
                .bookAuthor("Another Author")
                .bookPrice(new BigDecimal("24.99"))
                .quantityLeft(5)
                .seller(testSeller)
                .build();

        Book outOfStockBook = Book.builder()
                .bookId("book3")
                .bookName("Out of Stock Book")
                .bookAuthor("Some Author")
                .bookPrice(new BigDecimal("15.99"))
                .quantityLeft(0)
                .seller(testSeller)
                .build();

        List<Book> allBooks = new ArrayList<>();
        allBooks.add(testBook);
        allBooks.add(book2);
        allBooks.add(outOfStockBook);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = bookService.getAllAvailableBooks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(book -> book.getQuantityLeft() > 0));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no books available")
    public void testGetAllAvailableBooks_Empty() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        List<Book> result = bookService.getAllAvailableBooks();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should search books by query successfully")
    public void testSearchBooks_ByTitle() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(testBook);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = bookService.searchBooks("Test Book", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getBookName());
    }

    @Test
    @DisplayName("Should search books by author successfully")
    public void testSearchBooks_ByAuthor() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(testBook);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = bookService.searchBooks("Test Author", null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should search books with price filter")
    public void testSearchBooks_WithPriceFilter() {
        java.util.Map<String, Object> filters = new java.util.HashMap<>();
        filters.put("minPrice", "10.00");
        filters.put("maxPrice", "25.00");

        List<Book> allBooks = new ArrayList<>();
        allBooks.add(testBook);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = bookService.searchBooks(null, filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should search books with author filter")
    public void testSearchBooks_WithAuthorFilter() {
        java.util.Map<String, Object> filters = new java.util.HashMap<>();
        filters.put("author", "Test Author");

        List<Book> allBooks = new ArrayList<>();
        allBooks.add(testBook);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = bookService.searchBooks(null, filters);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return empty list when no books match search query")
    public void testSearchBooks_NoMatches() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(testBook);

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = bookService.searchBooks("Nonexistent Book", null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get purchased books for buyer successfully")
    public void testGetPurchasedBooks_Success() {
        User buyer = User.builder()
                .uid("buyer1")
                .username("buyer_user")
                .build();

        SoldBook soldBook = SoldBook.builder()
                .buyer(buyer)
                .book(testBook)
                .build();

        List<SoldBook> purchases = new ArrayList<>();
        purchases.add(soldBook);

        when(userRepository.findByUsername("buyer_user")).thenReturn(Optional.of(buyer));
        when(soldBookRepository.findByBuyerUid("buyer1")).thenReturn(purchases);

        List<SoldBook> result = bookService.getPurchasedBooks("buyer_user");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByUsername("buyer_user");
        verify(soldBookRepository, times(1)).findByBuyerUid("buyer1");
    }

    @Test
    @DisplayName("Should return empty list when buyer has no purchases")
    public void testGetPurchasedBooks_Empty() {
        User buyer = User.builder()
                .uid("buyer1")
                .username("buyer_user")
                .build();

        when(userRepository.findByUsername("buyer_user")).thenReturn(Optional.of(buyer));
        when(soldBookRepository.findByBuyerUid("buyer1")).thenReturn(new ArrayList<>());

        List<SoldBook> result = bookService.getPurchasedBooks("buyer_user");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get sold books for seller successfully")
    public void testGetSoldBooks_Success() {
        SoldBook soldBook = SoldBook.builder()
                .buyer(User.builder().uid("buyer1").build())
                .book(testBook)
                .build();

        List<SoldBook> sales = new ArrayList<>();
        sales.add(soldBook);

        when(userRepository.findByUsername("seller_user")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findByBookSellerUid("seller1")).thenReturn(sales);

        List<SoldBook> result = bookService.getSoldBooks("seller_user");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByUsername("seller_user");
        verify(soldBookRepository, times(1)).findByBookSellerUid("seller1");
    }

    @Test
    @DisplayName("Should return empty list when seller has no sales")
    public void testGetSoldBooks_Empty() {
        when(userRepository.findByUsername("seller_user")).thenReturn(Optional.of(testSeller));
        when(soldBookRepository.findByBookSellerUid("seller1")).thenReturn(new ArrayList<>());

        List<SoldBook> result = bookService.getSoldBooks("seller_user");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw exception when buyer not found for getting purchases")
    public void testGetPurchasedBooks_BuyerNotFound() {
        when(userRepository.findByUsername("nonexistent_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.getPurchasedBooks("nonexistent_buyer"));
        verify(userRepository).findByUsername("nonexistent_buyer");
    }
}

