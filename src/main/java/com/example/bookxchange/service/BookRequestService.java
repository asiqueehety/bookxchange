package com.example.bookxchange.service;

import com.example.bookxchange.dto.BookRequestDTO;
import com.example.bookxchange.entity.BookRequest;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRequestRepository;
import com.example.bookxchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookRequestService {

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new book request by a buyer
     */
    public BookRequest createBookRequest(BookRequestDTO bookRequestDTO, String buyerUsername) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with username: " + buyerUsername));

        BookRequest bookRequest = BookRequest.builder()
                .reqSubject(bookRequestDTO.getReqSubject())
                .reqDescription(bookRequestDTO.getReqDescription())
                .buyer(buyer)
                .createdAt(LocalDateTime.now())
                .build();

        return bookRequestRepository.save(bookRequest);
    }

    /**
     * Get all pending book requests (not yet fulfilled)
     */
    public List<BookRequest> getPendingRequests() {
        return bookRequestRepository.findByReqFulfillerIsNull();
    }

    /**
     * Get all book requests by a specific buyer
     */
    public List<BookRequest> getRequestsByBuyer(String buyerUsername) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with username: " + buyerUsername));
        
        return bookRequestRepository.findByBuyer(buyer);
    }

    /**
     * Get all requests fulfilled by a specific seller
     */
    public List<BookRequest> getRequestsFulfilledBy(String sellerUsername) {
        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with username: " + sellerUsername));
        
        return bookRequestRepository.findByReqFulfiller(seller);
    }

    /**
     * Fulfill a book request by a seller
     */
    public BookRequest fulfillRequest(String requestId, String sellerUsername) {
        BookRequest bookRequest = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + requestId));

        if (bookRequest.getReqFulfiller() != null) {
            throw new IllegalArgumentException("This request has already been fulfilled");
        }

        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with username: " + sellerUsername));

        bookRequest.setReqFulfiller(seller);
        return bookRequestRepository.save(bookRequest);
    }

    /**
     * Get a specific book request by ID
     */
    public BookRequest getRequestById(String requestId) {
        return bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + requestId));
    }

    /**
     * Update request status by fulfilling it
     */
    public BookRequest updateRequestStatus(String requestId, String newStatus) {
        BookRequest bookRequest = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + requestId));

        // Status is managed through the reqFulfiller field
        // "fulfilled" status = reqFulfiller is not null
        // If we're being asked to set fulfilled status, it should have been done via fulfillRequest()
        
        return bookRequest;
    }

    /**
     * Dismiss/Cancel a book request by the buyer
     */
    public void dismissRequest(String requestId, String buyerUsername) {
        BookRequest bookRequest = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id: " + requestId));

        if (!bookRequest.getBuyer().getUsername().equals(buyerUsername)) {
            throw new IllegalArgumentException("You are not authorized to dismiss this request");
        }

        bookRequestRepository.deleteById(requestId);
    }

    /**
     * Convert BookRequest entity to BookRequestDTO
     */
    public BookRequestDTO convertToDTO(BookRequest bookRequest) {
        return BookRequestDTO.builder()
                .reqId(bookRequest.getReqId())
                .reqSubject(bookRequest.getReqSubject())
                .reqDescription(bookRequest.getReqDescription())
                .buyerUsername(bookRequest.getBuyer().getUsername())
                .buyerEmail(bookRequest.getBuyer().getUserEmail())
                .reqFulfillerUsername(bookRequest.getReqFulfiller() != null ? bookRequest.getReqFulfiller().getUsername() : null)
                .createdAt(bookRequest.getCreatedAt())
                .build();
    }
}


