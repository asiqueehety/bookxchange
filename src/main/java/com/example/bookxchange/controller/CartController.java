package com.example.bookxchange.controller;

import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for shopping cart operations
 * Handles cart management for buyers
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * Helper method to get current authenticated user ID
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Returns the username
        }
        throw new IllegalArgumentException("User not authenticated");
    }

    /**
     * Add book to cart
     * POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            String userId = getCurrentUserId();
            String bookId = (String) request.get("bookId");
            Integer quantity = ((Number) request.getOrDefault("quantity", 1)).intValue();

            if (bookId == null || bookId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "bookId is required"));
            }

            CartItemDTO item = shoppingCartService.addToCart(userId, bookId, quantity);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "message", "Book added to cart successfully",
                            "item", item
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add to cart: " + e.getMessage()));
        }
    }

    /**
     * Remove book from cart
     * DELETE /api/cart/items/{cartItemId}
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable String cartItemId) {
        try {
            String userId = getCurrentUserId();
            shoppingCartService.removeFromCart(userId, cartItemId);
            return ResponseEntity.ok(Map.of("message", "Item removed from cart successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove from cart: " + e.getMessage()));
        }
    }

    /**
     * Update cart item quantity
     * PUT /api/cart/items/{cartItemId}
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable String cartItemId,
            @RequestBody Map<String, Object> request) {
        try {
            String userId = getCurrentUserId();
            Integer quantity = ((Number) request.get("quantity")).intValue();

            CartItemDTO updatedItem = shoppingCartService.updateCartItemQuantity(userId, cartItemId, quantity);
            return ResponseEntity.ok(Map.of(
                    "message", "Cart item updated successfully",
                    "item", updatedItem
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update cart item: " + e.getMessage()));
        }
    }

    /**
     * Get user's cart
     * GET /api/cart
     */
    @GetMapping
    public ResponseEntity<?> getCart() {
        try {
            String userId = getCurrentUserId();
            CartDTO cart = shoppingCartService.getCart(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve cart: " + e.getMessage()));
        }
    }

    /**
     * Get cart item count
     * GET /api/cart/count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCartItemCount() {
        try {
            String userId = getCurrentUserId();
            Integer count = shoppingCartService.getCartItemCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get cart count: " + e.getMessage()));
        }
    }

    /**
     * Clear user's cart
     * DELETE /api/cart
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        try {
            String userId = getCurrentUserId();
            shoppingCartService.clearCart(userId);
            return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to clear cart: " + e.getMessage()));
        }
    }
}

