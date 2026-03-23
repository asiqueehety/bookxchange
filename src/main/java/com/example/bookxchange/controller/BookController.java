package com.example.bookxchange.controller;

import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.service.BookService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;


    /**
     * Add a new book (Seller only)
     * POST /api/books/seller/add
     */
    @PostMapping("/seller/add")
    public ResponseEntity<?> createBook(@RequestBody BookDTO bookDTO) {
        try {
            String sellerId = getCurrentUserId();
            Book book = bookService.createBook(bookDTO, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookService.convertToDTO(book));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Helper method to get current authenticated user ID
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Returns the username, you may need to adjust based on your implementation
        }
        throw new IllegalArgumentException("User not authenticated");
    }

    /**
     * Get a single book by ID
     * GET /books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable String id) {
        try {
            Book book = bookService.getBookById(id);
            return ResponseEntity.ok(bookService.convertToDTO(book));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update a book (Seller only)
     * PUT /books/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable String id, @RequestBody BookDTO bookDTO) {
        try {
            String sellerId = getCurrentUserId();
            Book book = bookService.updateBook(id, bookDTO, sellerId);
            return ResponseEntity.ok(bookService.convertToDTO(book));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a book (Seller only)
     * DELETE /books/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        try {
            String sellerId = getCurrentUserId();
            bookService.deleteBook(id, sellerId);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Browse all available books with pagination
     * GET /books?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Book> allBooks = bookService.getAllAvailableBooks();
            Pageable pageable = PageRequest.of(page, size);
            
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allBooks.size());
            
            List<BookDTO> bookDTOs = allBooks.subList(start, end).stream()
                    .map(bookService::convertToDTO)
                    .collect(Collectors.toList());
            
            Page<BookDTO> bookPage = new PageImpl<>(bookDTOs, pageable, allBooks.size());
            
            return ResponseEntity.ok(bookPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Search books with optional filters
     * GET /books/search?query=java&minPrice=10&maxPrice=50&author=xyz&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> filters = new java.util.HashMap<>();
            if (minPrice != null) filters.put("minPrice", minPrice);
            if (maxPrice != null) filters.put("maxPrice", maxPrice);
            if (author != null) filters.put("author", author);
            
            List<Book> searchResults = bookService.searchBooks(query, filters);
            Pageable pageable = PageRequest.of(page, size);
            
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), searchResults.size());
            
            List<BookDTO> bookDTOs = searchResults.subList(start, end).stream()
                    .map(bookService::convertToDTO)
                    .collect(Collectors.toList());
            
            Page<BookDTO> bookPage = new PageImpl<>(bookDTOs, pageable, searchResults.size());
            
            return ResponseEntity.ok(bookPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all books by the current seller
     * GET /books/seller/my-books
     */
    @GetMapping("/seller/my-books")
    public ResponseEntity<?> getSellerBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String sellerId = getCurrentUserId();
            List<Book> sellerBooks = bookService.getBooksBySeller(sellerId);
            Pageable pageable = PageRequest.of(page, size);
            
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), sellerBooks.size());
            
            List<BookDTO> bookDTOs = sellerBooks.subList(start, end).stream()
                    .map(bookService::convertToDTO)
                    .collect(Collectors.toList());
            
            Page<BookDTO> bookPage = new PageImpl<>(bookDTOs, pageable, sellerBooks.size());
            
            return ResponseEntity.ok(bookPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Purchase a book (Buyer only)
     * POST /api/books/purchase
     */
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseBook(@RequestBody Map<String, Object> requestBody) {
        try {
            String buyerId = getCurrentUserId();
            String bookId = (String) requestBody.get("bookId");
            Integer quantity = ((Number) requestBody.getOrDefault("quantity", 1)).intValue();

            bookService.purchaseBook(bookId, buyerId, quantity);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Book purchased successfully", "bookId", bookId, "quantity", quantity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all purchased books for current buyer
     * GET /api/books/purchases
     */
    @GetMapping("/purchases")
    public ResponseEntity<?> getPurchasedBooks() {
        try {
            String buyerId = getCurrentUserId();
            var purchases = bookService.getPurchasedBooks(buyerId);
            
            var purchaseDTOs = purchases.stream()
                    .map(soldBook -> Map.of(
                        "soldId", soldBook.getSoldId(),
                        "book", bookService.convertToDTO(soldBook.getBook()),
                        "sellerUsername", soldBook.getBook().getSeller().getUsername()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(purchaseDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all sold books for current seller
     * GET /api/books/sold
     */
    @GetMapping("/sold")
    public ResponseEntity<?> getSoldBooks() {
        try {
            String sellerId = getCurrentUserId();
            var sold = bookService.getSoldBooks(sellerId);
            
            var soldDTOs = sold.stream()
                    .map(soldBook -> Map.of(
                        "soldId", soldBook.getSoldId(),
                        "book", bookService.convertToDTO(soldBook.getBook()),
                        "buyerUsername", soldBook.getBuyer().getUsername(),
                        "buyerEmail", soldBook.getBuyer().getUserEmail()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(soldDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
