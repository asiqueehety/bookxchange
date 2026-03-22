package com.example.bookxchange.repository;

import com.example.bookxchange.entity.SoldBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SoldBookRepository extends JpaRepository<SoldBook, String> {
    List<SoldBook> findByBuyerUid(String buyerUid);
    List<SoldBook> findByBookSellerUid(String sellerUid);
    boolean existsByBuyerUidAndBookBookId(String buyerUid, String bookId);
}
