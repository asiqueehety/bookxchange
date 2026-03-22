package com.example.bookxchange.service;

import com.example.bookxchange.dto.RegisterRequest;
import com.example.bookxchange.dto.UserDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User testUser;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID().toString();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setUserEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        testUser = User.builder()
            .uid(testUserId)
            .username("testuser")
            .userEmail("test@example.com")
            .passwordHash("hashedPassword")
            .userRole(UserRole.BUYER)
            .dateJoined(LocalDateTime.now())
            .build();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByUserEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getUserEmail());
        assertEquals(UserRole.BUYER, result.getUserRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterPasswordMismatch() {
        registerRequest.setConfirmPassword("different");

        assertThrows(Exception.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUsernameExists() throws Exception {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(Exception.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterEmailExists() throws Exception {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByUserEmail("test@example.com")).thenReturn(true);

        assertThrows(Exception.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByUsernameSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = authService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authService.getUserByUsername("nonexistent"));
    }

    @Test
    void testGetUserDTOByUsername() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDTO result = authService.getUserDTOByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getUserEmail());
        assertEquals(UserRole.BUYER, result.getUserRole());
    }

    @Test
    void testSwitchRoleSuccess() throws Exception {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = authService.switchRole(testUserId, UserRole.SELLER);

        assertNotNull(result);
        assertEquals(testUserId, result.getUid());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSwitchRoleUserNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authService.switchRole("nonexistent", UserRole.SELLER));
    }
}
