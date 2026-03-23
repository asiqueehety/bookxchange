package com.example.bookxchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private String cartItemId;
    private String bookId;
    private String bookName;
    private String bookAuthor;
    private BigDecimal bookPrice;
    private String bookCoverPic;
    private Integer quantity;
    private BigDecimal subtotal;
}

