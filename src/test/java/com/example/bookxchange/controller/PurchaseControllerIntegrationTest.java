package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.dto.PurchaseDTO;
import com.example.bookxchange.service.PurchaseService;
import com.example.bookxchange.service.ShoppingCartService;
import com.example.bookxchange.service.SoldBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PurchaseControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @MockBean
    private SoldBookService soldBookService;

    private CartDTO testCart;
    private CartItemDTO testCartItem;
    private PurchaseDTO testPurchase;

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

        testPurchase = PurchaseDTO.builder()
                .purchaseId("purchase123")
                .buyerId("buyer1")
                .buyerUsername("buyer1")
                .items(Arrays.asList(testCartItem))
                .totalItems(2)
                .totalAmount(BigDecimal.valueOf(59.98))
                .purchaseDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void processPurchase_Success() throws Exception {
        when(shoppingCartService.getCart("buyer1")).thenReturn(testCart);
        when(purchaseService.validatePurchase(anyList())).thenReturn(true);
        when(purchaseService.purchaseBooks(eq("buyer1"), anyList())).thenReturn(testPurchase);
        doNothing().when(shoppingCartService).clearCart("buyer1");

        mockMvc.perform(post("/api/purchases"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Purchase completed successfully"))
                .andExpect(jsonPath("$.purchase.purchaseId").value("purchase123"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void processPurchase_EmptyCart_ReturnsError() throws Exception {
        CartDTO emptyCart = CartDTO.builder()
                .items(Arrays.asList())
                .totalItems(0)
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(shoppingCartService.getCart("buyer1")).thenReturn(emptyCart);

        mockMvc.perform(post("/api/purchases"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cart is empty"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void validatePurchase_Success() throws Exception {
        when(purchaseService.validatePurchase(anyList())).thenReturn(true);

        mockMvc.perform(post("/api/purchases/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(testCartItem))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getPurchaseHistory_Success() throws Exception {
        List<PurchaseDTO> history = Arrays.asList(testPurchase);
        when(purchaseService.getPurchaseHistory("buyer1")).thenReturn(history);

        mockMvc.perform(get("/api/purchases/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.purchases[0].purchaseId").value("purchase123"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getTotalPurchaseAmount_Success() throws Exception {
        when(purchaseService.getTotalPurchaseAmount("buyer1")).thenReturn(BigDecimal.valueOf(299.99));

        mockMvc.perform(get("/api/purchases/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(299.99))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void getSellerSalesCount_Success() throws Exception {
        when(soldBookService.getSellerSalesCount("seller1")).thenReturn(15);

        mockMvc.perform(get("/api/purchases/seller/sales-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesCount").value(15))
                .andExpect(jsonPath("$.sellerId").value("seller1"));
    }

    @Test
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    void getSellerRevenue_Success() throws Exception {
        when(soldBookService.getSellerRevenue("seller1")).thenReturn(BigDecimal.valueOf(1500.00));

        mockMvc.perform(get("/api/purchases/seller/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(1500.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    @WithMockUser(username = "buyer1", roles = {"BUYER"})
    void getBuyerPurchaseCount_Success() throws Exception {
        when(soldBookService.getBuyerPurchaseCount("buyer1")).thenReturn(5);

        mockMvc.perform(get("/api/purchases/buyer/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseCount").value(5))
                .andExpect(jsonPath("$.buyerId").value("buyer1"));
    }
}