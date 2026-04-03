package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.service.AuthService;
import com.example.bookxchange.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PublicControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthService authService;

    private Book testBook;
    private BookDTO testBookDTO;
    private User testSeller;

    @BeforeEach
    void setUp() {
        testSeller = User.builder()
                .uid("seller123")
                .username("seller1")
                .userEmail("seller@example.com")
                .userRole(UserRole.SELLER)
                .build();

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
    void index_ReturnsLandingPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("landing"));
    }

    @Test
    void login_ReturnsLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void register_ReturnsRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @WithMockUser
    void bookDetail_AsAuthenticatedUser_ReturnsBookDetailPage() throws Exception {
        when(bookService.getBookById("book123")).thenReturn(testBook);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);

        mockMvc.perform(get("/books/book123"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book-detail"))
                .andExpect(model().attributeExists("book", "bookId", "isOwnBook", "sellerId"))
                .andExpect(model().attribute("book", testBookDTO));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void bookDetail_AsAnonymousUser_ReturnsBookDetailPage() throws Exception {
        when(bookService.getBookById("book123")).thenReturn(testBook);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);

        mockMvc.perform(get("/books/book123"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book-detail"))
                .andExpect(model().attribute("isOwnBook", false));
    }

    @Test
    @WithMockUser(username = "seller1")
    void bookDetail_WhenUserIsSeller_ShowsOwnBookFlag() throws Exception {
        when(bookService.getBookById("book123")).thenReturn(testBook);
        when(bookService.convertToDTO(any(Book.class))).thenReturn(testBookDTO);
        when(authService.getUserByUsername("seller1")).thenReturn(testSeller);

        mockMvc.perform(get("/books/book123"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book-detail"))
                .andExpect(model().attribute("isOwnBook", true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void bookDetail_BookNotFound_ReturnsError() throws Exception {
        when(bookService.getBookById("nonexistent"))
                .thenThrow(new IllegalArgumentException("Book not found"));

        mockMvc.perform(get("/books/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(view().name("landing"))
                .andExpect(model().attributeExists("error"));
    }
}