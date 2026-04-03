package com.example.bookxchange.repository;

import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserEmail(String userEmail);
    boolean existsByUsername(String username);
    boolean existsByUserEmail(String userEmail);
}
