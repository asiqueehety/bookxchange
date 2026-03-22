package com.example.bookxchange.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testBookCreation() {
        String bookId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Book book = Book.builder()
            .bookId(bookId)
            .bookName("Test Book")
            .bookAuthor("Test Author")
            .bookPrice(BigDecimal.valueOf(29.99))
            .quantityLeft(5)
            .createdAt(now)
            .build();

        assertNotNull(book);
        assertEquals(bookId, book.getBookId());
        assertEquals("Test Book", book.getBookName());
        assertEquals("Test Author", book.getBookAuthor());
        assertEquals(BigDecimal.valueOf(29.99), book.getBookPrice());
        assertEquals(5, book.getQuantityLeft());
        assertEquals(now, book.getCreatedAt());
    }

    @Test
    void testBookSetters() {
        Book book = new Book();

        book.setBookId(UUID.randomUUID().toString());
        book.setBookName("New Book");
        book.setBookAuthor("New Author");
        book.setBookPrice(BigDecimal.valueOf(19.99));
        book.setQuantityLeft(10);

        assertEquals("New Book", book.getBookName());
        assertEquals("New Author", book.getBookAuthor());
        assertEquals(BigDecimal.valueOf(19.99), book.getBookPrice());
        assertEquals(10, book.getQuantityLeft());
    }

    @Test
    void testBookPriceValidation() {
        Book book = Book.builder()
            .bookId(UUID.randomUUID().toString())
            .bookName("Expensive Book")
            .bookAuthor("Author")
            .bookPrice(BigDecimal.valueOf(999.99))
            .quantityLeft(1)
            .build();

        assertEquals(BigDecimal.valueOf(999.99), book.getBookPrice());
    }

    @Test
    void testBookQuantityDecrement() {
        Book book = Book.builder()
            .bookId(UUID.randomUUID().toString())
            .bookName("Test Book")
            .bookAuthor("Author")
            .bookPrice(BigDecimal.valueOf(25.00))
            .quantityLeft(5)
            .build();

        book.setQuantityLeft(book.getQuantityLeft() - 1);

        assertEquals(4, book.getQuantityLeft());
    }
}
