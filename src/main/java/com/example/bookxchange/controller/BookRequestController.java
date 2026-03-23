package com.example.bookxchange.controller;

import com.example.bookxchange.dto.BookRequestDTO;
import com.example.bookxchange.entity.BookRequest;
import com.example.bookxchange.service.BookRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for managing book requests
 * Handles buyer requests for books and seller fulfillment
 */
@RestController
@RequestMapping("/api/book-requests")
public class BookRequestController {

    @Autowired
    private BookRequestService bookRequestService;

    /**
     * Health check endpoint to verify requests table exists and is accessible
     * GET /api/book-requests/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            System.out.println("DEBUG: Health check - attempting to fetch pending requests");
            List<BookRequest> pending = bookRequestService.getPendingRequests();
            System.out.println("DEBUG: Health check successful - Found " + pending.size() + " pending requests");
            
            return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "pending_requests", pending.size()
            ));
        } catch (Exception e) {
            System.out.println("DEBUG: Health check FAILED - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "status", "unhealthy",
                        "error", e.getMessage()
                    ));
        }
    }

    /**
     * Helper method to get current authenticated user ID (username)
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new IllegalArgumentException("User not authenticated");
    }

    /**
     * Create a new book request (Buyer only)
     * POST /api/book-requests/buyer/requests
     * 
     * Request body:
     * {
     *   "reqSubject": "Book title or subject",
     *   "reqDescription": "Detailed description of what you're looking for"
     * }
     */
    @PostMapping("/buyer/requests")
    public ResponseEntity<?> createRequest(@RequestBody BookRequestDTO requestDTO) {
        try {
            String buyerUsername = getCurrentUserId();
            System.out.println("DEBUG: Creating book request for buyer: " + buyerUsername);
            System.out.println("DEBUG: Subject: " + requestDTO.getReqSubject());
            System.out.println("DEBUG: Description: " + requestDTO.getReqDescription());
            
            BookRequest createdRequest = bookRequestService.createBookRequest(requestDTO, buyerUsername);
            
            System.out.println("DEBUG: Request created successfully with ID: " + createdRequest.getReqId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookRequestService.convertToDTO(createdRequest));
        } catch (IllegalArgumentException e) {
            System.out.println("DEBUG: IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("DEBUG: Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create request: " + e.getMessage()));
        }
    }

    /**
     * Get a specific book request by ID
     * GET /api/book-requests/requests/{id}
     * 
     * SECURITY: Only the buyer who created the request or the seller who fulfilled it can view it
     */
    @GetMapping("/requests/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable String id) {
        try {
            String currentUser = getCurrentUserId();
            BookRequest request = bookRequestService.getRequestById(id);
            
            // Security: Ensure only the buyer or the fulfiller (seller) can view this request
            boolean isBuyer = request.getBuyer().getUsername().equals(currentUser);
            boolean isFulfiller = request.getReqFulfiller() != null && request.getReqFulfiller().getUsername().equals(currentUser);
            
            if (!isBuyer && !isFulfiller) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You do not have permission to view this request"));
            }
            
            return ResponseEntity.ok(bookRequestService.convertToDTO(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Request not found: " + e.getMessage()));
        }
    }

    /**
     * Fulfill a book request (Seller marks as fulfilled)
     * POST /api/book-requests/requests/{id}/fulfill
     * 
     * SECURITY: Only sellers can fulfill requests. Prevent fulfilling already fulfilled requests.
     * This endpoint marks a pending request as fulfilled by the current seller
     */
    @PostMapping("/requests/{id}/fulfill")
    public ResponseEntity<?> fulfillRequest(@PathVariable String id) {
        try {
            String sellerUsername = getCurrentUserId();
            
            // Get the request to verify it's still pending
            BookRequest request = bookRequestService.getRequestById(id);
            if (request.getReqFulfiller() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "This request has already been fulfilled by " + request.getReqFulfiller().getUsername()));
            }
            
            BookRequest fulfilledRequest = bookRequestService.fulfillRequest(id, sellerUsername);
            return ResponseEntity.ok(bookRequestService.convertToDTO(fulfilledRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fulfill request: " + e.getMessage()));
        }
    }

    /**
     * Dismiss/Cancel a book request (Buyer dismisses their own request)
     * DELETE /api/book-requests/requests/{id}
     * 
     * SECURITY: Only the buyer who created the request can dismiss it.
     * Sellers cannot dismiss requests, and buyers cannot dismiss fulfilled requests.
     */
    @DeleteMapping("/requests/{id}")
    public ResponseEntity<?> dismissRequest(@PathVariable String id) {
        try {
            String buyerUsername = getCurrentUserId();
            
            // Verify the request exists and belongs to the current buyer
            BookRequest request = bookRequestService.getRequestById(id);
            if (!request.getBuyer().getUsername().equals(buyerUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You do not have permission to dismiss this request"));
            }
            
            // Prevent dismissing already fulfilled requests
            if (request.getReqFulfiller() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Cannot dismiss a request that has already been fulfilled"));
            }
            
            bookRequestService.dismissRequest(id, buyerUsername);
            return ResponseEntity.ok(Map.of("message", "Request dismissed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to dismiss request: " + e.getMessage()));
        }
    }

    /**
     * Get all requests by current buyer
     * GET /api/book-requests/buyer/requests
     */
    @GetMapping("/buyer/requests")
    public ResponseEntity<?> getBuyerRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String buyerUsername = getCurrentUserId();
            List<BookRequest> requests = bookRequestService.getRequestsByBuyer(buyerUsername);
            List<BookRequestDTO> dtos = requests.stream()
                    .map(bookRequestService::convertToDTO)
                    .collect(Collectors.toList());

            // Apply pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), dtos.size());
            List<BookRequestDTO> paginatedList = dtos.subList(start, end);

            Page<BookRequestDTO> result = new PageImpl<>(
                    paginatedList,
                    pageable,
                    dtos.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve requests: " + e.getMessage()));
        }
    }

    /**
     * Get all pending requests (for sellers) - Simple non-paginated version
     * GET /api/book-requests/pending/all
     */
    @GetMapping("/pending/all")
    public ResponseEntity<?> getPendingRequestsSimple() {
        try {
            System.out.println("DEBUG: Fetching all pending requests");
            List<BookRequest> pendingRequests = bookRequestService.getPendingRequests();
            System.out.println("DEBUG: Found " + pendingRequests.size() + " pending requests");
            
            pendingRequests.forEach(req -> {
                System.out.println("DEBUG: Request ID: " + req.getReqId() + ", Subject: " + req.getReqSubject() + ", Buyer: " + req.getBuyer().getUsername());
            });
            
            List<BookRequestDTO> requestDTOs = pendingRequests.stream()
                    .map(bookRequestService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(requestDTOs);
        } catch (Exception e) {
            System.out.println("DEBUG: Exception fetching pending requests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve pending requests: " + e.getMessage()));
        }
    }

    /**
     * Get all pending requests (for sellers)
     * GET /api/book-requests/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<BookRequest> pendingRequests = bookRequestService.getPendingRequests();
            List<BookRequestDTO> dtos = pendingRequests.stream()
                    .map(bookRequestService::convertToDTO)
                    .collect(Collectors.toList());

            // Apply pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), dtos.size());
            List<BookRequestDTO> paginatedList = dtos.subList(start, end);

            Page<BookRequestDTO> result = new PageImpl<>(
                    paginatedList,
                    pageable,
                    dtos.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve pending requests: " + e.getMessage()));
        }
    }

    /**
     * Get all requests fulfilled by current seller
     * GET /api/book-requests/seller/fulfilled
     */
    @GetMapping("/seller/fulfilled")
    public ResponseEntity<?> getSellerFulfilledRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String sellerUsername = getCurrentUserId();
            List<BookRequest> fulfilledRequests = bookRequestService.getRequestsFulfilledBy(sellerUsername);
            List<BookRequestDTO> dtos = fulfilledRequests.stream()
                    .map(bookRequestService::convertToDTO)
                    .collect(Collectors.toList());

            // Apply pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), dtos.size());
            List<BookRequestDTO> paginatedList = dtos.subList(start, end);

            Page<BookRequestDTO> result = new PageImpl<>(
                    paginatedList,
                    pageable,
                    dtos.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve fulfilled requests: " + e.getMessage()));
        }
    }

    /**
     * Search/filter requests
     * GET /api/book-requests/search
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRequests(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<BookRequest> allRequests;

            // Get pending or fulfilled requests
            if ("pending".equalsIgnoreCase(status)) {
                allRequests = bookRequestService.getPendingRequests();
            } else if ("all".equalsIgnoreCase(status)) {
                allRequests = bookRequestService.getPendingRequests();
                // In a real implementation, you might want to add getAllRequests() method
            } else {
                allRequests = bookRequestService.getPendingRequests();
            }

            // Filter by subject if provided
            List<BookRequestDTO> dtos = allRequests.stream()
                    .filter(req -> subject == null || 
                            req.getReqSubject().toLowerCase().contains(subject.toLowerCase()))
                    .map(bookRequestService::convertToDTO)
                    .collect(Collectors.toList());

            // Apply pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), dtos.size());
            List<BookRequestDTO> paginatedList = dtos.subList(start, end);

            Page<BookRequestDTO> result = new PageImpl<>(
                    paginatedList,
                    pageable,
                    dtos.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    /**
     * Get request statistics
     * GET /api/book-requests/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getRequestStatistics() {
        try {
            List<BookRequest> allPending = bookRequestService.getPendingRequests();

            Map<String, Object> stats = Map.of(
                    "totalPending", allPending.size(),
                    "totalRequests", allPending.size(),
                    "mostRequestedCategory", "General"  // Can be enhanced
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }
}

