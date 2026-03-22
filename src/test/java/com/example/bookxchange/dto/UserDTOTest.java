package com.example.bookxchange.dto;

import com.example.bookxchange.entity.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testUserDTOCreation() {
        String uid = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        UserDTO userDTO = UserDTO.builder()
            .uid(uid)
            .username("testuser")
            .userEmail("test@example.com")
            .profilePic(null)
            .userRole(UserRole.BUYER)
            .dateJoined(now)
            .build();

        assertNotNull(userDTO);
        assertEquals(uid, userDTO.getUid());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("test@example.com", userDTO.getUserEmail());
        assertEquals(UserRole.BUYER, userDTO.getUserRole());
        assertEquals(now, userDTO.getDateJoined());
    }

    @Test
    void testUserDTOSetters() {
        UserDTO userDTO = new UserDTO();
        String uid = UUID.randomUUID().toString();

        userDTO.setUid(uid);
        userDTO.setUsername("newuser");
        userDTO.setUserEmail("newuser@example.com");
        userDTO.setUserRole(UserRole.SELLER);

        assertEquals(uid, userDTO.getUid());
        assertEquals("newuser", userDTO.getUsername());
        assertEquals("newuser@example.com", userDTO.getUserEmail());
        assertEquals(UserRole.SELLER, userDTO.getUserRole());
    }

    @Test
    void testUserDTOWithProfilePic() {
        UserDTO userDTO = new UserDTO();
        userDTO.setProfilePic("path/to/pic.jpg");

        assertEquals("path/to/pic.jpg", userDTO.getProfilePic());
    }
}
