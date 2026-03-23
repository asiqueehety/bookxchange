package com.example.bookxchange.repository;

import com.example.bookxchange.entity.BookRequest;
import com.example.bookxchange.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, String> {
    List<BookRequest> findByBuyer(User buyer);
    List<BookRequest> findByReqFulfiller(User reqFulfiller);
    List<BookRequest> findByReqFulfillerIsNull();
    @Query("SELECT COUNT(br) FROM BookRequest br WHERE br.buyer.uid = ?1")
    long countByBuyerId(String buyerId);
}
