package com.example.bookxchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookId;

    @Column(nullable = false)
    private String bookName;

    @Column(name = "book_cover_pic")
    private String bookCoverPic;

    @Column(nullable = false)
    private String bookAuthor;

    @Column(name = "book_short_preview", length = 1000)
    private String bookShortPreview;

    @Column(nullable = false)
    private BigDecimal bookPrice;

    @Column(nullable = false)
    private Integer quantityLeft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
