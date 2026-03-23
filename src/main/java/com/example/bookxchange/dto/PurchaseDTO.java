package com.example.bookxchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO {
    private String purchaseId;
    private String buyerId;
    private String buyerUsername;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private LocalDateTime purchaseDate;
    private String status;
}

