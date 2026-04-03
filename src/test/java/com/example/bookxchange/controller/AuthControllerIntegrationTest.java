package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_Success() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("username", "testuser")
                        .param("userEmail", "test@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("success"));
    }

    @Test
    void register_UserAlreadyExists_ReturnsError() throws Exception {
        // Create existing user
        User existingUser = User.builder()
                .username("existinguser")
                .userEmail("existing@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .userRole(UserRole.BUYER)
                .build();
        userRepository.save(existingUser);

        mockMvc.perform(post("/auth/register")
                        .param("username", "existinguser")
                        .param("userEmail", "new@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void register_PasswordsDoNotMatch_ReturnsError() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("username", "testuser")
                        .param("userEmail", "test@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "differentpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("error"));
    }
}