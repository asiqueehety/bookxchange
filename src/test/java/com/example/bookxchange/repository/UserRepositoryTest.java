package com.example.bookxchange.repository;

import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .uid(UUID.randomUUID().toString())
            .username("testuser")
            .userEmail("test@example.com")
            .passwordHash("hashedPassword")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .build();
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> found = userRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean exists = userRepository.existsByUsername("testuser");

        assertTrue(exists);
    }

    @Test
    void testExistsByUserEmail() {
        when(userRepository.existsByUserEmail("test@example.com")).thenReturn(true);

        boolean exists = userRepository.existsByUserEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User saved = userRepository.save(testUser);

        assertNotNull(saved.getUid());
        assertEquals("testuser", saved.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
