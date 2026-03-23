package com.example.bookxchange.repository;

import com.example.bookxchange.entity.SoldBook;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SoldBookRepository extends JpaRepository<SoldBook, String> {
    List<SoldBook> findByBuyerUid(String buyerUid);
    List<SoldBook> findByBookSellerUid(String sellerUid);
    List<SoldBook> findByBuyer(User buyer);
    List<SoldBook> findByBook(Book book);
    boolean existsByBuyerUidAndBookBookId(String buyerUid, String bookId);
    @Query("SELECT COUNT(sb) FROM SoldBook sb WHERE sb.buyer.uid = ?1")
    long countByBuyerId(String buyerId);
    @Query("SELECT COUNT(sb) FROM SoldBook sb WHERE sb.book.seller.uid = ?1")
    long countBySellerIdFromBooks(String sellerId);
}
