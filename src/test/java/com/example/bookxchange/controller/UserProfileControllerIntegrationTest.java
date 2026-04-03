package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.UserStatsDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import com.example.bookxchange.service.AuthService;
import com.example.bookxchange.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserProfileControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private UserStatsDTO testUserStats;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .uid("user123")
                .username("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedPassword123")
                .userRole(UserRole.BUYER)
                .dateJoined(LocalDateTime.now())
                .build();

        testUserStats = UserStatsDTO.builder()
                .uid("user123")
                .username("testuser")
                .userEmail("test@example.com")
                .userRole(UserRole.BUYER)
                .dateJoined(LocalDateTime.now())
                .totalBooksPosted(5)
                .totalBooksSold(2)
                .totalBooksPurchased(3)
                .totalRequestsMade(1)
                .build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void viewProfile_Success() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.getUserStats("user123")).thenReturn(testUserStats);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("userStats"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void updateProfile_Success() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.updateProfile(eq("user123"), any())).thenReturn(testUser);

        mockMvc.perform(post("/user/profile/update")
                        .param("username", "updateduser")
                        .param("userEmail", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void uploadProfilePicture_Success() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.uploadProfilePicture(eq("user123"), any())).thenReturn("path/to/image.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "profilePicture",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/user/profile/upload-picture")
                        .file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void uploadProfilePicture_EmptyFile_ReturnsError() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);

        MockMultipartFile file = new MockMultipartFile(
                "profilePicture",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/user/profile/upload-picture")
                        .file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void deleteProfilePicture_Success() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).deleteProfilePicture("user123");

        mockMvc.perform(post("/user/profile/delete-picture"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void changePassword_Success() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).changePassword(eq("user123"), any());

        mockMvc.perform(post("/user/profile/change-password")
                        .param("oldPassword", "oldpass123")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "newpass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"BUYER"})
    void deleteAccount_Success() throws Exception {
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).deleteAccount("user123");

        mockMvc.perform(post("/user/profile/delete-account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}