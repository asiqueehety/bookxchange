package com.example.bookxchange.repository;

import com.example.bookxchange.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findBySellerUid(String sellerUid);

    @Query("SELECT b FROM Book b WHERE b.seller.username = :username")
    List<Book> findBySellerUsername(@Param("username") String username);

    List<Book> findAll();
    @Query("SELECT COUNT(b) FROM Book b WHERE b.seller.uid = ?1")
    long countBySellerId(String sellerId);
}
