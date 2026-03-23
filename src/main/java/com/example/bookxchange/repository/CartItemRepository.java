package com.example.bookxchange.repository;

import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.CartItem;
import com.example.bookxchange.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByUserAndIsActive(User user, Boolean isActive);
    Optional<CartItem> findByUserAndBookAndIsActive(User user, Book book, Boolean isActive);
    void deleteByUserAndIsActive(User user, Boolean isActive);
}

