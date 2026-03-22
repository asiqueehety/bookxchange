package com.example.bookxchange.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        String uid = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
            .uid(uid)
            .username("testuser")
            .userEmail("test@example.com")
            .passwordHash("hashedPassword")
            .userRole(UserRole.BUYER)
            .profilePic(null)
            .dateJoined(now)
            .build();

        assertNotNull(user);
        assertEquals(uid, user.getUid());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getUserEmail());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals(UserRole.BUYER, user.getUserRole());
        assertEquals(now, user.getDateJoined());
    }

    @Test
    void testUserSetterMethods() {
        User user = new User();
        String uid = UUID.randomUUID().toString();

        user.setUid(uid);
        user.setUsername("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRole.SELLER);

        assertEquals(uid, user.getUid());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getUserEmail());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals(UserRole.SELLER, user.getUserRole());
    }

    @Test
    void testUserRoleEnum() {
        assertEquals(UserRole.BUYER, UserRole.BUYER);
        assertEquals(UserRole.SELLER, UserRole.SELLER);
        assertEquals(UserRole.ADMIN, UserRole.ADMIN);

        assertTrue(UserRole.valueOf("BUYER") == UserRole.BUYER);
        assertTrue(UserRole.valueOf("SELLER") == UserRole.SELLER);
        assertTrue(UserRole.valueOf("ADMIN") == UserRole.ADMIN);
    }
}
