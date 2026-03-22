package com.example.bookxchange.repository;

import com.example.bookxchange.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findBySellerUid(String sellerUid);
    List<Book> findAll();
}
