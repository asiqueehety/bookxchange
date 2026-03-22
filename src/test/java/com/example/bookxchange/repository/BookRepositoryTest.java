package com.example.bookxchange.repository;

import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookRepositoryTest {

    @Mock
    private BookRepository bookRepository;

    private User seller;
    private Book testBook;

    @BeforeEach
    void setUp() {
        seller = User.builder()
            .uid(UUID.randomUUID().toString())
            .username("seller")
            .userEmail("seller@example.com")
            .passwordHash("hash")
            .userRole(UserRole.SELLER)
            .dateJoined(LocalDateTime.now())
            .build();

        testBook = Book.builder()
            .bookId(UUID.randomUUID().toString())
            .bookName("Test Book")
            .bookAuthor("Test Author")
            .bookPrice(BigDecimal.valueOf(25.00))
            .quantityLeft(5)
            .seller(seller)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void testFindById() {
        when(bookRepository.findById(testBook.getBookId())).thenReturn(Optional.of(testBook));

        Optional<Book> found = bookRepository.findById(testBook.getBookId());

        assertTrue(found.isPresent());
        assertEquals("Test Book", found.get().getBookName());
    }

    @Test
    void testFindBySellerUid() {
        List<Book> books = new ArrayList<>();
        books.add(testBook);
        when(bookRepository.findBySellerUid(seller.getUid())).thenReturn(books);

        List<Book> result = bookRepository.findBySellerUid(seller.getUid());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(b -> b.getBookId().equals(testBook.getBookId())));
    }

    @Test
    void testSaveBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book saved = bookRepository.save(testBook);

        assertNotNull(saved.getBookId());
        assertEquals("Test Book", saved.getBookName());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        testBook.setQuantityLeft(3);
        when(bookRepository.save(testBook)).thenReturn(testBook);

        Book updated = bookRepository.save(testBook);

        assertEquals(3, updated.getQuantityLeft());
    }

    @Test
    void testDeleteBook() {
        bookRepository.deleteById(testBook.getBookId());

        verify(bookRepository, times(1)).deleteById(testBook.getBookId());
    }
}
