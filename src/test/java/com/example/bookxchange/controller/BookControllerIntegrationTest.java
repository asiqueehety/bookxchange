package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.SoldBook;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import com.example.bookxchange.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private BookService bookService;

    @Autowired
    private UserRepository userRepository;

    private User testSeller;
    private Book testBook;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testSeller = User.builder()
                .username("seller1")
                .userEmail("seller@example.com")
                .passwordHash("hashedPassword123")
                .userRole(UserRole.SELLER)
                .build();
        userRepository.save(testSeller);

        testBook = Book.builder()
                .bookId("book123")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(BigDecimal.valueOf(29.99))
                .quantityLeft(10)
                .seller(testSeller)
                .build();

        testBookDTO = BookDTO.builder()
                .bookId("book123")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(BigDecimal.valueOf(29.99))
                .quantityLeft(10)
                .sellerId(testSeller.getUid())
                .sellerUsername(testSeller.getUsername())
                .build();
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void createBook_Success() throws Exception {
        when(bookService.createBook(any(BookDTO.class), anyString())).thenReturn(testBook);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);

        mockMvc.perform(post("/api/books/seller/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookName").value("Test Book"))
                .andExpect(jsonPath("$.bookPrice").value(29.99));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void createBook_Unauthorized_ReturnsError() throws Exception {
        when(bookService.createBook(any(BookDTO.class), anyString()))
                .thenThrow(new IllegalArgumentException("User is not a seller"));

        mockMvc.perform(post("/api/books/seller/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    void getBookById_Success() throws Exception {
        when(bookService.getBookById("book123")).thenReturn(testBook);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);

        mockMvc.perform(get("/api/books/book123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName").value("Test Book"));
    }

    @Test
    @WithMockUser
    void getBookById_NotFound_Returns404() throws Exception {
        when(bookService.getBookById("nonexistent"))
                .thenThrow(new IllegalArgumentException("Book not found"));

        mockMvc.perform(get("/api/books/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void updateBook_Success() throws Exception {
        Book updatedBook = testBook;
        updatedBook.setBookName("Updated Book");

        BookDTO updatedDTO = testBookDTO;
        updatedDTO.setBookName("Updated Book");

        when(bookService.updateBook(eq("book123"), any(BookDTO.class), eq("seller1")))
                .thenReturn(updatedBook);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/books/book123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName").value("Updated Book"));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void deleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook("book123", "seller1");

        mockMvc.perform(delete("/api/books/book123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));
    }

    @Test
    @WithMockUser
    void getAllBooks_ReturnsPaginatedResults() throws Exception {
        List<Book> books = Arrays.asList(testBook);
        when(bookService.getAllAvailableBooks()).thenReturn(books);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    void searchBooks_WithFilters_ReturnsResults() throws Exception {
        List<Book> books = Arrays.asList(testBook);
        Map<String, Object> filters = new HashMap<>();

        when(bookService.searchBooks(anyString(), anyMap())).thenReturn(books);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);

        mockMvc.perform(get("/api/books/search")
                        .param("query", "Test")
                        .param("minPrice", "10")
                        .param("maxPrice", "50")
                        .param("author", "Test Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void purchaseBook_Success() throws Exception {
        Map<String, Object> purchaseRequest = new HashMap<>();
        purchaseRequest.put("bookId", "book123");
        purchaseRequest.put("quantity", 2);

        SoldBook soldBook = SoldBook.builder()
                .soldId("sold123")
                .book(testBook)
                .buyer(User.builder().uid("buyer1").username("buyer1").build())
                .build();
        
        when(bookService.purchaseBook("book123", "buyer1", 2)).thenReturn(soldBook);

        mockMvc.perform(post("/api/books/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book purchased successfully"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getPurchasedBooks_Success() throws Exception {
        when(bookService.getPurchasedBooks("buyer1")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/books/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void getSoldBooks_Success() throws Exception {
        when(bookService.getSoldBooks("seller1")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/books/sold"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}