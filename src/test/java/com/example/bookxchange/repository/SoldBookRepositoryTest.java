package com.example.bookxchange.repository;

import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.SoldBook;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoldBookRepositoryTest {

    @Mock
    private SoldBookRepository soldBookRepository;

    private User buyer;
    private User seller;
    private Book book;
    private SoldBook soldBook;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
            .uid(UUID.randomUUID().toString())
            .username("buyer")
            .userEmail("buyer@example.com")
            .passwordHash("hash")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .build();

        seller = User.builder()
            .uid(UUID.randomUUID().toString())
            .username("seller")
            .userEmail("seller@example.com")
            .passwordHash("hash")
            .userRole(UserRole.SELLER)
            .dateJoined(LocalDateTime.now())
            .build();

        book = Book.builder()
            .bookId(UUID.randomUUID().toString())
            .bookName("Test Book")
            .bookAuthor("Test Author")
            .bookPrice(BigDecimal.valueOf(25.00))
            .quantityLeft(5)
            .seller(seller)
            .createdAt(LocalDateTime.now())
            .build();

        soldBook = SoldBook.builder()
            .soldId(UUID.randomUUID().toString())
            .buyer(buyer)
            .book(book)
            .build();
    }

    @Test
    void testFindByBuyerUid() {
        List<SoldBook> books = new ArrayList<>();
        books.add(soldBook);
        when(soldBookRepository.findByBuyerUid(buyer.getUid())).thenReturn(books);

        List<SoldBook> result = soldBookRepository.findByBuyerUid(buyer.getUid());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(buyer.getUid(), result.get(0).getBuyer().getUid());
    }

    @Test
    void testExistsByBuyerUidAndBookBookId() {
        when(soldBookRepository.existsByBuyerUidAndBookBookId(buyer.getUid(), book.getBookId()))
            .thenReturn(true);

        boolean exists = soldBookRepository.existsByBuyerUidAndBookBookId(buyer.getUid(), book.getBookId());

        assertTrue(exists);
    }

    @Test
    void testExistsByBuyerUidAndBookBookIdNotFound() {
        when(soldBookRepository.existsByBuyerUidAndBookBookId(buyer.getUid(), "nonexistent"))
            .thenReturn(false);

        boolean exists = soldBookRepository.existsByBuyerUidAndBookBookId(buyer.getUid(), "nonexistent");

        assertFalse(exists);
    }

    @Test
    void testSaveSoldBook() {
        when(soldBookRepository.save(any(SoldBook.class))).thenReturn(soldBook);

        SoldBook saved = soldBookRepository.save(soldBook);

        assertNotNull(saved.getSoldId());
        assertEquals(buyer.getUid(), saved.getBuyer().getUid());
        verify(soldBookRepository, times(1)).save(any(SoldBook.class));
    }
}
