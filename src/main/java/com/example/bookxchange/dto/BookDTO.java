package com.example.bookxchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private String bookId;
    private String bookName;
    private String bookCoverPic;
    private String bookAuthor;
    private String bookShortPreview;
    private BigDecimal bookPrice;
    private Integer quantityLeft;
    private String sellerId;
    private String sellerUsername;
    private LocalDateTime createdAt;
}
