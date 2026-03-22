package com.example.bookxchange.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegisterRequestTest {

    @Test
    void testRegisterRequestCreation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setUserEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        assertNotNull(request);
        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getUserEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("password123", request.getConfirmPassword());
    }

    @Test
    void testRegisterRequestSetters() {
        RegisterRequest request = new RegisterRequest();

        request.setUsername("newuser");
        request.setUserEmail("newuser@example.com");
        request.setPassword("newpassword");
        request.setConfirmPassword("newpassword");

        assertEquals("newuser", request.getUsername());
        assertEquals("newuser@example.com", request.getUserEmail());
        assertEquals("newpassword", request.getPassword());
        assertEquals("newpassword", request.getConfirmPassword());
    }
}
