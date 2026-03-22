package com.example.bookxchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sold_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldBook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String soldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
