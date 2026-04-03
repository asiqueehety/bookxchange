package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.BookRequestDTO;
import com.example.bookxchange.entity.BookRequest;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import com.example.bookxchange.service.BookRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookRequestControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private BookRequestService bookRequestService;

    @Autowired
    private UserRepository userRepository;

    private User testBuyer;
    private User testSeller;
    private BookRequest testRequest;
    private BookRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        testBuyer = User.builder()
                .username("buyer1")
                .userEmail("buyer@example.com")
                .passwordHash("hashedPassword123")
                .userRole(UserRole.BUYER)
                .build();
        userRepository.save(testBuyer);

        testSeller = User.builder()
                .username("seller1")
                .userEmail("seller@example.com")
                .passwordHash("hashedPassword123")
                .userRole(UserRole.SELLER)
                .build();
        userRepository.save(testSeller);

        testRequest = BookRequest.builder()
                .reqId("req123")
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .buyer(testBuyer)
                .createdAt(LocalDateTime.now())
                .build();

        testRequestDTO = BookRequestDTO.builder()
                .reqId("req123")
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .buyerUsername(testBuyer.getUsername())
                .buyerEmail(testBuyer.getUserEmail())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void createRequest_Success() throws Exception {
        when(bookRequestService.createBookRequest(any(BookRequestDTO.class), eq("buyer1")))
                .thenReturn(testRequest);
        when(bookRequestService.convertToDTO(any(BookRequest.class))).thenReturn(testRequestDTO);

        mockMvc.perform(post("/api/book-requests/buyer/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reqSubject").value("Looking for Java Book"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getBuyerRequests_Success() throws Exception {
        List<BookRequest> requests = Arrays.asList(testRequest);
        when(bookRequestService.getRequestsByBuyer("buyer1")).thenReturn(requests);
        when(bookRequestService.convertToDTO(any(BookRequest.class))).thenReturn(testRequestDTO);

        mockMvc.perform(get("/api/book-requests/buyer/requests")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void getPendingRequests_Success() throws Exception {
        List<BookRequest> pendingRequests = Arrays.asList(testRequest);
        when(bookRequestService.getPendingRequests()).thenReturn(pendingRequests);
        when(bookRequestService.convertToDTO(any(BookRequest.class))).thenReturn(testRequestDTO);

        mockMvc.perform(get("/api/book-requests/pending")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void fulfillRequest_Success() throws Exception {
        // Create a fresh unfulfilled request
        BookRequest unfulfilled = BookRequest.builder()
                .reqId("req123")
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .buyer(testBuyer)
                .createdAt(LocalDateTime.now())
                .reqFulfiller(null)  // Explicitly null
                .build();
        
        // Create fulfilled version for response
        BookRequest fulfilledRequest = BookRequest.builder()
                .reqId("req123")
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .buyer(testBuyer)
                .createdAt(LocalDateTime.now())
                .reqFulfiller(testSeller)
                .build();
        
        BookRequestDTO fulfilledDTO = BookRequestDTO.builder()
                .reqId("req123")
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .buyerUsername(testBuyer.getUsername())
                .buyerEmail(testBuyer.getUserEmail())
                .createdAt(LocalDateTime.now())
                .reqFulfillerUsername("seller1")
                .build();

        when(bookRequestService.getRequestById("req123")).thenReturn(unfulfilled);
        when(bookRequestService.fulfillRequest("req123", "seller1")).thenReturn(fulfilledRequest);
        when(bookRequestService.convertToDTO(any(BookRequest.class))).thenReturn(fulfilledDTO);

        mockMvc.perform(post("/api/book-requests/requests/req123/fulfill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reqFulfillerUsername").value("seller1"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void dismissRequest_Success() throws Exception {
        when(bookRequestService.getRequestById("req123")).thenReturn(testRequest);
        doNothing().when(bookRequestService).dismissRequest("req123", "buyer1");

        mockMvc.perform(delete("/api/book-requests/requests/req123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Request dismissed successfully"));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void getSellerFulfilledRequests_Success() throws Exception {
        BookRequest fulfilledRequest = testRequest;
        fulfilledRequest.setReqFulfiller(testSeller);
        List<BookRequest> fulfilledRequests = Arrays.asList(fulfilledRequest);

        when(bookRequestService.getRequestsFulfilledBy("seller1")).thenReturn(fulfilledRequests);
        when(bookRequestService.convertToDTO(any(BookRequest.class))).thenReturn(testRequestDTO);

        mockMvc.perform(get("/api/book-requests/seller/fulfilled")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void healthCheck_Success() throws Exception {
        List<BookRequest> pendingRequests = Arrays.asList(testRequest);
        when(bookRequestService.getPendingRequests()).thenReturn(pendingRequests);

        mockMvc.perform(get("/api/book-requests/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.pending_requests").value(1));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getRequestById_AsBuyer_Success() throws Exception {
        when(bookRequestService.getRequestById("req123")).thenReturn(testRequest);
        when(bookRequestService.convertToDTO(any(BookRequest.class))).thenReturn(testRequestDTO);

        mockMvc.perform(get("/api/book-requests/requests/req123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reqSubject").value("Looking for Java Book"));
    }

    @Test
    @WithMockUser(username = "differentUser", roles = {"BUYER"})
    void getRequestById_Unauthorized_Returns403() throws Exception {
        when(bookRequestService.getRequestById("req123")).thenReturn(testRequest);

        mockMvc.perform(get("/api/book-requests/requests/req123"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You do not have permission to view this request"));
    }
}