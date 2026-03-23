package com.example.bookxchange.controller;

import com.example.bookxchange.dto.ChangePasswordRequest;
import com.example.bookxchange.dto.UpdateProfileRequest;
import com.example.bookxchange.dto.UserStatsDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.service.AuthService;
import com.example.bookxchange.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private User testUser;
    private UserStatsDTO userStatsDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();

        testUser = User.builder()
            .uid("user-123")
            .username("testuser")
            .userEmail("test@example.com")
            .passwordHash("hashedpassword")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .profilePic(null)
            .build();

        userStatsDTO = UserStatsDTO.builder()
            .uid("user-123")
            .username("testuser")
            .userEmail("test@example.com")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .totalBooksPosted(0)
            .totalBooksSold(0)
            .totalBooksPurchased(2)
            .totalRequestsMade(1)
            .profilePic(null)
            .build();
    }

    private void mockAuthentication(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        when(auth.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testViewProfile_Success() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.getUserStats("user-123")).thenReturn(userStatsDTO);

        mockMvc.perform(get("/user/profile"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/profile"))
            .andExpect(model().attributeExists("userStats"));

        verify(userService, times(1)).getUserStats("user-123");
    }

    @Test
    void testViewProfile_UserNotFound() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenThrow(new Exception("User not found"));

        mockMvc.perform(get("/user/profile"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.updateProfile(eq("user-123"), any(UpdateProfileRequest.class)))
            .thenReturn(testUser);

        mockMvc.perform(post("/user/profile/update")
            .param("username", "newusername")
            .param("userEmail", "newemail@example.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).updateProfile(eq("user-123"), any(UpdateProfileRequest.class));
    }

    @Test
    void testUpdateProfile_UsernameAlreadyTaken() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.updateProfile(eq("user-123"), any(UpdateProfileRequest.class)))
            .thenThrow(new Exception("Username already taken"));

        mockMvc.perform(post("/user/profile/update")
            .param("username", "existinguser")
            .param("userEmail", "newemail@example.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).updateProfile(eq("user-123"), any(UpdateProfileRequest.class));
    }

    @Test
    void testUploadProfilePicture_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "profilePicture",
            "profile.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.uploadProfilePicture(eq("user-123"), any(MultipartFile.class)))
            .thenReturn("profile-uuid.jpg");

        mockMvc.perform(multipart("/user/profile/upload-picture")
            .file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).uploadProfilePicture(eq("user-123"), any(MultipartFile.class));
    }

    @Test
    void testUploadProfilePicture_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "profilePicture",
            "profile.jpg",
            "image/jpeg",
            new byte[0]
        );

        mockMvc.perform(multipart("/user/profile/upload-picture")
            .file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));
    }

    @Test
    void testDeleteProfilePicture_Success() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).deleteProfilePicture("user-123");

        mockMvc.perform(post("/user/profile/delete-picture"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).deleteProfilePicture("user-123");
    }

    @Test
    void testChangePassword_Success() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).changePassword(eq("user-123"), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/user/profile/change-password")
            .param("oldPassword", "admin123")
            .param("newPassword", "newpass123")
            .param("confirmPassword", "newpass123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).changePassword(eq("user-123"), any(ChangePasswordRequest.class));
    }

    @Test
    void testChangePassword_PasswordMismatch() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doThrow(new Exception("New passwords do not match"))
            .when(userService).changePassword(eq("user-123"), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/user/profile/change-password")
            .param("oldPassword", "admin123")
            .param("newPassword", "newpass123")
            .param("confirmPassword", "wrongpass123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).changePassword(eq("user-123"), any(ChangePasswordRequest.class));
    }

    @Test
    void testDeleteAccount_Success() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doNothing().when(userService).deleteAccount("user-123");

        mockMvc.perform(post("/user/profile/delete-account"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));

        verify(userService, times(1)).deleteAccount("user-123");
    }

    @Test
    void testDeleteAccount_Error() throws Exception {
        mockAuthentication("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        doThrow(new Exception("Error deleting account"))
            .when(userService).deleteAccount("user-123");

        mockMvc.perform(post("/user/profile/delete-account"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/profile"));

        verify(userService, times(1)).deleteAccount("user-123");
    }
}
