package com.example.bookxchange.controller;

import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.dto.PurchaseDTO;
import com.example.bookxchange.service.PurchaseService;
import com.example.bookxchange.service.ShoppingCartService;
import com.example.bookxchange.service.SoldBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for purchase operations
 * Handles purchase processing and history
 */
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private SoldBookService soldBookService;

    /**
     * Helper method to get current authenticated user ID
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new IllegalArgumentException("User not authenticated");
    }

    /**
     * Process purchase from cart
     * POST /api/purchases
     */
    @PostMapping
    public ResponseEntity<?> processPurchase() {
        try {
            String userId = getCurrentUserId();

            // Get user's cart
            CartDTO cart = shoppingCartService.getCart(userId);

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Cart is empty"));
            }

            // Validate purchase
            purchaseService.validatePurchase(cart.getItems());

            // Process purchase
            PurchaseDTO purchase = purchaseService.purchaseBooks(userId, cart.getItems());

            // Clear cart after successful purchase
            shoppingCartService.clearCart(userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Purchase completed successfully",
                            "purchase", purchase
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process purchase: " + e.getMessage()));
        }
    }

    /**
     * Validate purchase before processing
     * POST /api/purchases/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validatePurchase(@RequestBody List<CartItemDTO> items) {
        try {
            boolean isValid = purchaseService.validatePurchase(items);
            return ResponseEntity.ok(Map.of(
                    "valid", isValid,
                    "message", "Purchase is valid"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("valid", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false, "error", "Validation failed: " + e.getMessage()));
        }
    }

    /**
     * Get purchase history for current user
     * GET /api/purchases/history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getPurchaseHistory() {
        try {
            String userId = getCurrentUserId();
            List<PurchaseDTO> history = purchaseService.getPurchaseHistory(userId);
            return ResponseEntity.ok(Map.of(
                    "total", history.size(),
                    "purchases", history
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve purchase history: " + e.getMessage()));
        }
    }

    /**
     * Get total purchase amount for current user
     * GET /api/purchases/total
     */
    @GetMapping("/total")
    public ResponseEntity<?> getTotalPurchaseAmount() {
        try {
            String userId = getCurrentUserId();
            BigDecimal totalAmount = purchaseService.getTotalPurchaseAmount(userId);
            return ResponseEntity.ok(Map.of(
                    "totalAmount", totalAmount,
                    "currency", "USD"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to calculate total: " + e.getMessage()));
        }
    }

    /**
     * Get seller's sales count
     * GET /api/purchases/seller/sales-count
     */
    @GetMapping("/seller/sales-count")
    public ResponseEntity<?> getSellerSalesCount() {
        try {
            String sellerId = getCurrentUserId();
            Integer salesCount = soldBookService.getSellerSalesCount(sellerId);
            return ResponseEntity.ok(Map.of(
                    "salesCount", salesCount,
                    "sellerId", sellerId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get sales count: " + e.getMessage()));
        }
    }

    /**
     * Get seller's total revenue
     * GET /api/purchases/seller/revenue
     */
    @GetMapping("/seller/revenue")
    public ResponseEntity<?> getSellerRevenue() {
        try {
            String sellerId = getCurrentUserId();
            BigDecimal revenue = soldBookService.getSellerRevenue(sellerId);
            return ResponseEntity.ok(Map.of(
                    "totalRevenue", revenue,
                    "currency", "USD",
                    "sellerId", sellerId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to calculate revenue: " + e.getMessage()));
        }
    }

    /**
     * Get buyer's purchase count
     * GET /api/purchases/buyer/count
     */
    @GetMapping("/buyer/count")
    public ResponseEntity<?> getBuyerPurchaseCount() {
        try {
            String buyerId = getCurrentUserId();
            Integer count = soldBookService.getBuyerPurchaseCount(buyerId);
            return ResponseEntity.ok(Map.of(
                    "purchaseCount", count,
                    "buyerId", buyerId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get purchase count: " + e.getMessage()));
        }
    }
}

