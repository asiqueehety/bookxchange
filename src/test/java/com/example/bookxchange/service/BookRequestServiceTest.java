package com.example.bookxchange.service;

import com.example.bookxchange.dto.BookRequestDTO;
import com.example.bookxchange.entity.BookRequest;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRequestRepository;
import com.example.bookxchange.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("BookRequestService Unit Tests")
public class BookRequestServiceTest {

    @Mock
    private BookRequestRepository bookRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookRequestService bookRequestService;

    private User testBuyer;
    private User testSeller;
    private BookRequestDTO bookRequestDTO;
    private BookRequest testRequest;

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

        // Setup test book request DTO
        bookRequestDTO = BookRequestDTO.builder()
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .build();

        // Setup test book request
        testRequest = BookRequest.builder()
                .reqId("req1")
                .reqSubject("Looking for Java Book")
                .reqDescription("Need a comprehensive Java programming book")
                .buyer(testBuyer)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create a new book request successfully")
    public void testCreateBookRequest_Success() {
        when(userRepository.findByUsername("buyer_user")).thenReturn(Optional.of(testBuyer));
        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(testRequest);

        BookRequest result = bookRequestService.createBookRequest(bookRequestDTO, "buyer_user");

        assertNotNull(result);
        assertEquals("Looking for Java Book", result.getReqSubject());
        assertEquals("Need a comprehensive Java programming book", result.getReqDescription());
        assertEquals(testBuyer, result.getBuyer());
        verify(bookRequestRepository, times(1)).save(any(BookRequest.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found during creation")
    public void testCreateBookRequest_BuyerNotFound() {
        when(userRepository.findByUsername("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookRequestService.createBookRequest(bookRequestDTO, "unknown_buyer");
        });
    }

    @Test
    @DisplayName("Should get pending requests successfully")
    public void testGetPendingRequests_Success() {
        List<BookRequest> pendingRequests = new ArrayList<>();
        pendingRequests.add(testRequest);

        when(bookRequestRepository.findByReqFulfillerIsNull()).thenReturn(pendingRequests);

        List<BookRequest> result = bookRequestService.getPendingRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Looking for Java Book", result.get(0).getReqSubject());
        verify(bookRequestRepository, times(1)).findByReqFulfillerIsNull();
    }

    @Test
    @DisplayName("Should return empty list when no pending requests")
    public void testGetPendingRequests_Empty() {
        when(bookRequestRepository.findByReqFulfillerIsNull()).thenReturn(new ArrayList<>());

        List<BookRequest> result = bookRequestService.getPendingRequests();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get requests by buyer successfully")
    public void testGetRequestsByBuyer_Success() {
        List<BookRequest> buyerRequests = new ArrayList<>();
        buyerRequests.add(testRequest);

        when(userRepository.findByUsername("buyer_user")).thenReturn(Optional.of(testBuyer));
        when(bookRequestRepository.findByBuyer(testBuyer)).thenReturn(buyerRequests);

        List<BookRequest> result = bookRequestService.getRequestsByBuyer("buyer_user");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByUsername("buyer_user");
        verify(bookRequestRepository, times(1)).findByBuyer(testBuyer);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when buyer not found")
    public void testGetRequestsByBuyer_BuyerNotFound() {
        when(userRepository.findByUsername("unknown_buyer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookRequestService.getRequestsByBuyer("unknown_buyer");
        });
    }

    @Test
    @DisplayName("Should get requests fulfilled by seller successfully")
    public void testGetRequestsFulfilledBy_Success() {
        testRequest.setReqFulfiller(testSeller);
        List<BookRequest> fulfilledRequests = new ArrayList<>();
        fulfilledRequests.add(testRequest);

        when(userRepository.findByUsername("seller_user")).thenReturn(Optional.of(testSeller));
        when(bookRequestRepository.findByReqFulfiller(testSeller)).thenReturn(fulfilledRequests);

        List<BookRequest> result = bookRequestService.getRequestsFulfilledBy("seller_user");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRequestRepository, times(1)).findByReqFulfiller(testSeller);
    }

    @Test
    @DisplayName("Should fulfill request successfully")
    public void testFulfillRequest_Success() {
        when(bookRequestRepository.findById("req1")).thenReturn(Optional.of(testRequest));
        when(userRepository.findByUsername("seller_user")).thenReturn(Optional.of(testSeller));
        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(testRequest);

        BookRequest result = bookRequestService.fulfillRequest("req1", "seller_user");

        assertNotNull(result);
        assertEquals(testSeller, result.getReqFulfiller());
        verify(bookRequestRepository, times(1)).save(any(BookRequest.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when request not found during fulfill")
    public void testFulfillRequest_RequestNotFound() {
        when(bookRequestRepository.findById("non_existent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookRequestService.fulfillRequest("non_existent", "seller_user");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when request already fulfilled")
    public void testFulfillRequest_AlreadyFulfilled() {
        testRequest.setReqFulfiller(testSeller);

        when(bookRequestRepository.findById("req1")).thenReturn(Optional.of(testRequest));

        assertThrows(IllegalArgumentException.class, () -> {
            bookRequestService.fulfillRequest("req1", "seller_user");
        });
    }

    @Test
    @DisplayName("Should get request by ID successfully")
    public void testGetRequestById_Success() {
        when(bookRequestRepository.findById("req1")).thenReturn(Optional.of(testRequest));

        BookRequest result = bookRequestService.getRequestById("req1");

        assertNotNull(result);
        assertEquals("Looking for Java Book", result.getReqSubject());
        verify(bookRequestRepository, times(1)).findById("req1");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when request not found")
    public void testGetRequestById_NotFound() {
        when(bookRequestRepository.findById("non_existent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookRequestService.getRequestById("non_existent");
        });
    }

    @Test
    @DisplayName("Should dismiss request successfully")
    public void testDismissRequest_Success() {
        when(bookRequestRepository.findById("req1")).thenReturn(Optional.of(testRequest));

        bookRequestService.dismissRequest("req1", "buyer_user");

        verify(bookRequestRepository, times(1)).deleteById("req1");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when dismissing non-existent request")
    public void testDismissRequest_NotFound() {
        when(bookRequestRepository.findById("non_existent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookRequestService.dismissRequest("non_existent", "buyer_user");
        });
    }

    @Test
    @DisplayName("Should convert BookRequest to DTO successfully")
    public void testConvertToDTO() {
        BookRequestDTO result = bookRequestService.convertToDTO(testRequest);

        assertNotNull(result);
        assertEquals("Looking for Java Book", result.getReqSubject());
        assertEquals("Need a comprehensive Java programming book", result.getReqDescription());
        assertEquals("buyer_user", result.getBuyerUsername());
    }
}

