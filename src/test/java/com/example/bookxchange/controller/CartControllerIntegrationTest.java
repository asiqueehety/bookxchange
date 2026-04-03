package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private ShoppingCartService shoppingCartService;

    private CartDTO testCart;
    private CartItemDTO testCartItem;

    @BeforeEach
    void setUp() {
        testCartItem = CartItemDTO.builder()
                .cartItemId("item123")
                .bookId("book123")
                .bookName("Test Book")
                .bookPrice(BigDecimal.valueOf(29.99))
                .quantity(2)
                .subtotal(BigDecimal.valueOf(59.98))
                .build();

        testCart = CartDTO.builder()
                .items(Arrays.asList(testCartItem))
                .totalItems(2)
                .totalPrice(BigDecimal.valueOf(59.98))
                .build();
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void addToCart_Success() throws Exception {
        when(shoppingCartService.addToCart(eq("buyer1"), eq("book123"), eq(1)))
                .thenReturn(testCartItem);

        Map<String, Object> request = new HashMap<>();
        request.put("bookId", "book123");
        request.put("quantity", 1);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book added to cart successfully"))
                .andExpect(jsonPath("$.item.bookName").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void addToCart_WithoutBookId_ReturnsError() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("quantity", 1);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("bookId is required"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void removeFromCart_Success() throws Exception {
        doNothing().when(shoppingCartService).removeFromCart("buyer1", "item123");

        mockMvc.perform(delete("/api/cart/items/item123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item removed from cart successfully"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void updateCartItemQuantity_Success() throws Exception {
        CartItemDTO updatedItem = testCartItem;
        updatedItem.setQuantity(3);
        updatedItem.setSubtotal(BigDecimal.valueOf(89.97));

        when(shoppingCartService.updateCartItemQuantity(eq("buyer1"), eq("item123"), eq(3)))
                .thenReturn(updatedItem);

        Map<String, Object> request = new HashMap<>();
        request.put("quantity", 3);

        mockMvc.perform(put("/api/cart/items/item123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart item updated successfully"))
                .andExpect(jsonPath("$.item.quantity").value(3));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getCart_Success() throws Exception {
        when(shoppingCartService.getCart("buyer1")).thenReturn(testCart);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPrice").value(59.98));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getCartItemCount_Success() throws Exception {
        when(shoppingCartService.getCartItemCount("buyer1")).thenReturn(2);

        mockMvc.perform(get("/api/cart/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void clearCart_Success() throws Exception {
        doNothing().when(shoppingCartService).clearCart("buyer1");

        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart cleared successfully"));
    }
}