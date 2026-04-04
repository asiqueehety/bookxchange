package com.example.bookxchange.controller;

import com.example.bookxchange.dto.UserDTO;
import com.example.bookxchange.entity.BookRequest;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.BookRequestRepository;
import com.example.bookxchange.repository.SoldBookRepository;
import com.example.bookxchange.repository.UserRepository;
import com.example.bookxchange.service.BookRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for admin operations
 * Handles user management, book management, and request oversight
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private SoldBookRepository soldBookRepository;

    @Autowired
    private BookRequestService bookRequestService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get dashboard statistics
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            long totalUsers = userRepository.count();
            long totalBooks = bookRepository.count();
            long totalSales = soldBookRepository.count();
            long activeRequests = bookRequestRepository.findByReqFulfillerIsNull().size();

            Map<String, Object> stats = Map.of(
                    "totalUsers", totalUsers,
                    "totalBooks", totalBooks,
                    "totalSales", totalSales,
                    "activeRequests", activeRequests
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }

    /**
     * Get all users with pagination and search
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<User> allUsers = userRepository.findAll();

            // Apply search filter
            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                allUsers = allUsers.stream()
                        .filter(u -> u.getUsername().toLowerCase().contains(searchLower) ||
                                u.getUserEmail().toLowerCase().contains(searchLower))
                        .collect(Collectors.toList());
            }

            // Apply pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), allUsers.size());

            List<Map<String, Object>> usersList = allUsers.subList(start, end).stream()
                    .map(this::convertUserToMap)
                    .collect(Collectors.toList());

            Page<Map<String, Object>> result = new PageImpl<>(
                    usersList,
                    pageable,
                    allUsers.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    /**
     * Delete a user by ID
     * DELETE /api/admin/users/{userId}
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            // Don't allow deleting the last admin
            if (user.getUserRole() == UserRole.ADMIN) {
                long adminCount = userRepository.findAll().stream()
                        .filter(u -> u.getUserRole() == UserRole.ADMIN)
                        .count();
                if (adminCount <= 1) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Cannot delete the last admin user"));
                }
            }

            userRepository.delete(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully",
                    "userId", userId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * Update user role
     * PUT /api/admin/users/{userId}/role
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            String newRole = request.get("role");
            UserRole role = UserRole.valueOf(newRole.toUpperCase());

            // Don't allow changing last admin
            if (user.getUserRole() == UserRole.ADMIN && role != UserRole.ADMIN) {
                long adminCount = userRepository.findAll().stream()
                        .filter(u -> u.getUserRole() == UserRole.ADMIN)
                        .count();
                if (adminCount <= 1) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Cannot change the last admin's role"));
                }
            }

            user.setUserRole(role);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User role updated successfully",
                    "userId", userId,
                    "newRole", role.toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user role: " + e.getMessage()));
        }
    }

    /**
     * Get all book requests with admin details
     * GET /api/admin/requests
     */
    @GetMapping("/requests")
    public ResponseEntity<?> getBookRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<BookRequest> allRequests = bookRequestRepository.findAll();

            // Apply pagination
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), allRequests.size());

            List<Map<String, Object>> requestsList = allRequests.subList(start, end).stream()
                    .map(this::convertRequestToMap)
                    .collect(Collectors.toList());

            Page<Map<String, Object>> result = new PageImpl<>(
                    requestsList,
                    pageable,
                    allRequests.size()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch requests: " + e.getMessage()));
        }
    }

    /**
     * Delete a book request
     * DELETE /api/admin/requests/{requestId}
     */
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<?> deleteRequest(@PathVariable String requestId) {
        try {
            BookRequest request = bookRequestRepository.findById(requestId)
                    .orElseThrow(() -> new Exception("Request not found"));

            bookRequestRepository.delete(request);

            return ResponseEntity.ok(Map.of(
                    "message", "Request deleted successfully",
                    "requestId", requestId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete request: " + e.getMessage()));
        }
    }

    /**
     * Delete a book by ID
     * DELETE /api/admin/books/{bookId}
     */
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable String bookId) {
        try {
            bookRepository.findById(bookId)
                    .orElseThrow(() -> new Exception("Book not found"));

            bookRepository.deleteById(bookId);

            return ResponseEntity.ok(Map.of(
                    "message", "Book deleted successfully",
                    "bookId", bookId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete book: " + e.getMessage()));
        }
    }

    /**
     * Get user details for editing
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            Map<String, Object> userMap = convertUserToMap(user);
            return ResponseEntity.ok(userMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user: " + e.getMessage()));
        }
    }

    /**
     * Update user information (admin can update any user)
     * PUT /api/admin/users/{userId}
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            String username = request.get("username");
            String userEmail = request.get("userEmail");

            // Validate username uniqueness (excluding current user)
            if (username != null && !username.isEmpty() && !username.equals(user.getUsername())) {
                if (userRepository.existsByUsername(username)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Username already exists"));
                }
                user.setUsername(username);
            }

            // Validate email uniqueness (excluding current user)
            if (userEmail != null && !userEmail.isEmpty() && !userEmail.equals(user.getUserEmail())) {
                if (userRepository.existsByUserEmail(userEmail)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Email already exists"));
                }
                user.setUserEmail(userEmail);
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "user", convertUserToMap(user)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    // Helper methods

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", user.getUid());
        map.put("username", user.getUsername());
        map.put("userEmail", user.getUserEmail());
        map.put("userRole", user.getUserRole().toString());
        map.put("dateJoined", user.getDateJoined());
        map.put("profilePic", user.getProfilePic());
        return map;
    }

    private Map<String, Object> convertRequestToMap(BookRequest req) {
        Map<String, Object> map = new HashMap<>();
        map.put("reqId", req.getReqId());
        map.put("reqSubject", req.getReqSubject());
        map.put("reqDescription", req.getReqDescription());
        map.put("buyerUsername", req.getBuyer() != null ? req.getBuyer().getUsername() : "N/A");
        map.put("fulfillerUsername", req.getReqFulfiller() != null ? req.getReqFulfiller().getUsername() : "Pending");
        map.put("status", req.getReqFulfiller() != null ? "Fulfilled" : "Pending");
        return map;
    }
}
