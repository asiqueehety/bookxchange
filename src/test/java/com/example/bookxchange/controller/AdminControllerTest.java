package com.example.bookxchange.controller;

import com.example.bookxchange.entity.*;
import com.example.bookxchange.repository.*;
import com.example.bookxchange.service.BookRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookRequestRepository bookRequestRepository;

    @Mock
    private SoldBookRepository soldBookRepository;

    @Mock
    private BookRequestService bookRequestService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testAdmin;
    private User testUser;
    private Book testBook;
    private BookRequest testRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        testAdmin = User.builder()
                .uid("admin-1")
                .username("admin")
                .userEmail("admin@test.com")
                .passwordHash("hashed")
                .userRole(UserRole.ADMIN)
                .dateJoined(LocalDateTime.now())
                .build();

        testUser = User.builder()
                .uid("user-1")
                .username("testuser")
                .userEmail("user@test.com")
                .passwordHash("hashed")
                .userRole(UserRole.BUYER)
                .dateJoined(LocalDateTime.now())
                .build();

        testBook = Book.builder()
                .bookId("book-1")
                .bookName("Test Book")
                .bookAuthor("Test Author")
                .bookPrice(new BigDecimal("10.0"))
                .quantityLeft(5)
                .seller(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = BookRequest.builder()
                .reqId("req-1")
                .reqSubject("Looking for Test Book")
                .reqDescription("I need this book")
                .buyer(testUser)
                .reqFulfiller(null)
                .build();
    }

    @Test
    void testGetStats() {
        // Arrange
        when(userRepository.count()).thenReturn(5L);
        when(bookRepository.count()).thenReturn(10L);
        when(soldBookRepository.count()).thenReturn(3L);
        when(bookRequestRepository.findByReqFulfillerIsNull()).thenReturn(Collections.singletonList(testRequest));

        // Act
        ResponseEntity<?> response = adminController.getStats();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(5L, body.get("totalUsers"));
        assertEquals(10L, body.get("totalBooks"));
        assertEquals(3L, body.get("totalSales"));
        assertEquals(1L, body.get("activeRequests"));
        verify(userRepository, times(1)).count();
        verify(bookRepository, times(1)).count();
        verify(soldBookRepository, times(1)).count();
    }

    @Test
    void testGetStatsException() {
        // Arrange
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = adminController.getStats();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("error"));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testAdmin, testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        ResponseEntity<?> response = adminController.getAllUsers(null, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsersWithSearch() {
        // Arrange
        List<User> filteredUsers = Collections.singletonList(testUser);
        when(userRepository.findAll()).thenReturn(filteredUsers);

        // Act
        ResponseEntity<?> response = adminController.getAllUsers("testuser", 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAllUsersPaginated() {
        // Arrange
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            User user = User.builder()
                    .uid("user-" + i)
                    .username("user" + i)
                    .userEmail("user" + i + "@test.com")
                    .passwordHash("hashed")
                    .userRole(UserRole.BUYER)
                    .dateJoined(LocalDateTime.now())
                    .build();
            users.add(user);
        }
        when(userRepository.findAll()).thenReturn(users);

        // Act
        ResponseEntity<?> response = adminController.getAllUsers(null, 1, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteUserSuccess() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        // Only mock findAll() if the user role is ADMIN
        lenient().when(userRepository.findAll()).thenReturn(Arrays.asList(testAdmin, testUser));

        // Act
        ResponseEntity<?> response = adminController.deleteUser("user-1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUserNotFound() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = adminController.deleteUser("nonexistent");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteLastAdminFails() {
        // Arrange
        when(userRepository.findById("admin-1")).thenReturn(Optional.of(testAdmin));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testAdmin));

        // Act
        ResponseEntity<?> response = adminController.deleteUser("admin-1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.get("error").toString().contains("last admin"));
    }

    @Test
    void testUpdateUserRole() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        lenient().when(userRepository.findAll()).thenReturn(Arrays.asList(testAdmin, testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Map<String, String> request = new HashMap<>();
        request.put("role", "SELLER");

        // Act
        ResponseEntity<?> response = adminController.updateUserRole("user-1", request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserRoleInvalidRole() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Map<String, String> request = new HashMap<>();
        request.put("role", "INVALID");

        // Act - The controller will catch the IllegalArgumentException and return an error response
        ResponseEntity<?> response = adminController.updateUserRole("user-1", request);

        // Assert - Since the controller catches exceptions, we should get INTERNAL_SERVER_ERROR
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("error"));
    }

    @Test
    void testGetBookRequests() {
        // Arrange
        List<BookRequest> requests = Collections.singletonList(testRequest);
        when(bookRequestRepository.findAll()).thenReturn(requests);

        // Act
        ResponseEntity<?> response = adminController.getBookRequests(0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteRequest() {
        // Arrange
        when(bookRequestRepository.findById("req-1")).thenReturn(Optional.of(testRequest));

        // Act
        ResponseEntity<?> response = adminController.deleteRequest("req-1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookRequestRepository, times(1)).delete(testRequest);
    }

    @Test
    void testDeleteRequestNotFound() {
        // Arrange
        when(bookRequestRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = adminController.deleteRequest("nonexistent");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteBook() {
        // Arrange
        when(bookRepository.findById("book-1")).thenReturn(Optional.of(testBook));

        // Act
        ResponseEntity<?> response = adminController.deleteBook("book-1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookRepository, times(1)).deleteById("book-1");
    }

    @Test
    void testDeleteBookNotFound() {
        // Arrange
        when(bookRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = adminController.deleteBook("nonexistent");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetUserDetails() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<?> response = adminController.getUserDetails("user-1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("testuser", body.get("username"));
        assertEquals("user@test.com", body.get("userEmail"));
    }

    @Test
    void testGetUserDetailsNotFound() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = adminController.getUserDetails("nonexistent");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByUserEmail("newemail@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Map<String, String> request = new HashMap<>();
        request.put("username", "newuser");
        request.put("userEmail", "newemail@test.com");

        // Act
        ResponseEntity<?> response = adminController.updateUser("user-1", request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserUsernameTaken() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("username", "existinguser");

        // Act
        ResponseEntity<?> response = adminController.updateUser("user-1", request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.get("error").toString().contains("already exists"));
    }

    @Test
    void testUpdateUserEmailTaken() {
        // Arrange
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserEmail("existing@test.com")).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("userEmail", "existing@test.com");

        // Act
        ResponseEntity<?> response = adminController.updateUser("user-1", request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateUserNotFound() {
        // Arrange
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Map<String, String> request = new HashMap<>();
        request.put("username", "newuser");

        // Act
        ResponseEntity<?> response = adminController.updateUser("nonexistent", request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
