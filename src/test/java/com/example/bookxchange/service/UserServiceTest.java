package com.example.bookxchange.service;

import com.example.bookxchange.dto.ChangePasswordRequest;
import com.example.bookxchange.dto.UpdateProfileRequest;
import com.example.bookxchange.dto.UserStatsDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.BookRequestRepository;
import com.example.bookxchange.repository.SoldBookRepository;
import com.example.bookxchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private SoldBookRepository soldBookRepository;

    @Mock
    private BookRequestRepository bookRequestRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileUploadService fileUploadService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .uid("user-123")
            .username("testuser")
            .userEmail("test@example.com")
            .passwordHash("$2a$10$encryptedpassword")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .profilePic(null)
            .build();
    }

    // ==================== getUserById Tests ====================

    @Test
    void testGetUserById_Success() throws Exception {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        User result = userService.getUserById("user-123");

        assertNotNull(result);
        assertEquals("user-123", result.getUid());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById("user-123");
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserById("nonexistent");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById("nonexistent");
    }

    // ==================== getUserStats Tests ====================

    @Test
    void testGetUserStats_Success() throws Exception {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(bookRepository.countBySellerId("user-123")).thenReturn(5L);
        when(soldBookRepository.countBySellerIdFromBooks("user-123")).thenReturn(3L);
        when(soldBookRepository.countByBuyerId("user-123")).thenReturn(2L);
        when(bookRequestRepository.countByBuyerId("user-123")).thenReturn(1L);

        UserStatsDTO stats = userService.getUserStats("user-123");

        assertNotNull(stats);
        assertEquals("user-123", stats.getUid());
        assertEquals("testuser", stats.getUsername());
        assertEquals("test@example.com", stats.getUserEmail());
        assertEquals(5, stats.getTotalBooksPosted());
        assertEquals(3, stats.getTotalBooksSold());
        assertEquals(2, stats.getTotalBooksPurchased());
        assertEquals(1, stats.getTotalRequestsMade());

        verify(userRepository, times(1)).findById("user-123");
        verify(bookRepository, times(1)).countBySellerId("user-123");
        verify(soldBookRepository, times(1)).countBySellerIdFromBooks("user-123");
        verify(soldBookRepository, times(1)).countByBuyerId("user-123");
        verify(bookRequestRepository, times(1)).countByBuyerId("user-123");
    }

    @Test
    void testGetUserStats_UserNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserStats("nonexistent");
        });

        assertEquals("User not found", exception.getMessage());
    }

    // ==================== updateProfile Tests ====================

    @Test
    void testUpdateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("newusername");
        request.setUserEmail("newemail@example.com");

        User updatedUser = User.builder()
            .uid("user-123")
            .username("newusername")
            .userEmail("newemail@example.com")
            .passwordHash("$2a$10$encryptedpassword")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .profilePic(null)
            .build();

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByUserEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateProfile("user-123", request);

        assertNotNull(result);
        assertEquals("newusername", result.getUsername());
        assertEquals("newemail@example.com", result.getUserEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UsernameAlreadyTaken() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("existinguser");
        request.setUserEmail("test@example.com");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateProfile("user-123", request);
        });

        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateProfile_EmailAlreadyInUse() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("testuser");
        request.setUserEmail("existing@example.com");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserEmail("existing@example.com")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.updateProfile("user-123", request);
        });

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== changePassword Tests ====================

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("admin123");
        request.setNewPassword("newpass123");
        request.setConfirmPassword("newpass123");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("admin123", testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode("newpass123")).thenReturn("$2a$10$newencryptedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword("user-123", request);

        verify(userRepository, times(1)).findById("user-123");
        verify(passwordEncoder, times(1)).matches("admin123", "$2a$10$encryptedpassword");
        verify(passwordEncoder, times(1)).encode("newpass123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("admin123");
        request.setNewPassword("newpass123");
        request.setConfirmPassword("wrongpass123");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("user-123", request);
        });

        assertEquals("New passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_PasswordTooShort() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("admin123");
        request.setNewPassword("short");
        request.setConfirmPassword("short");

        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("user-123", request);
        });

        assertEquals("Password must be at least 6 characters", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_IncorrectOldPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongpassword");
        request.setNewPassword("newpass123");
        request.setConfirmPassword("newpass123");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("user-123", request);
        });

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== uploadProfilePicture Tests ====================

    @Test
    void testUploadProfilePicture_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "profilePicture",
            "profile.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(fileUploadService.saveFile(file)).thenReturn("profile-uuid.jpg");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = userService.uploadProfilePicture("user-123", file);

        assertNotNull(result);
        assertEquals("profile-uuid.jpg", result);
        verify(fileUploadService, times(1)).saveFile(file);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUploadProfilePicture_DeleteOldPicture() throws Exception {
        testUser.setProfilePic("old-picture.jpg");

        MockMultipartFile file = new MockMultipartFile(
            "profilePicture",
            "profile.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(fileUploadService.saveFile(file)).thenReturn("profile-uuid.jpg");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(fileUploadService).deleteFile("old-picture.jpg");

        String result = userService.uploadProfilePicture("user-123", file);

        assertNotNull(result);
        assertEquals("profile-uuid.jpg", result);
        verify(fileUploadService, times(1)).deleteFile("old-picture.jpg");
        verify(fileUploadService, times(1)).saveFile(file);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ==================== deleteProfilePicture Tests ====================

    @Test
    void testDeleteProfilePicture_Success() throws Exception {
        testUser.setProfilePic("profile-uuid.jpg");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        doNothing().when(fileUploadService).deleteFile("profile-uuid.jpg");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deleteProfilePicture("user-123");

        verify(fileUploadService, times(1)).deleteFile("profile-uuid.jpg");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteProfilePicture_NoPictureExists() throws Exception {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        userService.deleteProfilePicture("user-123");

        verify(fileUploadService, never()).deleteFile(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteProfilePicture_UserNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            userService.deleteProfilePicture("nonexistent");
        });

        verify(fileUploadService, never()).deleteFile(anyString());
    }

    // ==================== deleteAccount Tests ====================

    @Test
    void testDeleteAccount_Success() throws Exception {
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById("user-123");

        userService.deleteAccount("user-123");

        verify(userRepository, times(1)).deleteById("user-123");
        verify(fileUploadService, never()).deleteFile(anyString());
    }

    @Test
    void testDeleteAccount_WithProfilePicture() throws Exception {
        testUser.setProfilePic("profile-uuid.jpg");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        doNothing().when(fileUploadService).deleteFile("profile-uuid.jpg");
        doNothing().when(userRepository).deleteById("user-123");

        userService.deleteAccount("user-123");

        verify(fileUploadService, times(1)).deleteFile("profile-uuid.jpg");
        verify(userRepository, times(1)).deleteById("user-123");
    }

    @Test
    void testDeleteAccount_UserNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            userService.deleteAccount("nonexistent");
        });

        verify(userRepository, never()).deleteById(anyString());
    }
}
