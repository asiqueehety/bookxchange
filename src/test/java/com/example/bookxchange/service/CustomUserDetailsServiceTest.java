package com.example.bookxchange.service;

import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

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
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("nonexistent"));

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsernameWithBuyerRole() {
        testUser.setUserRole(UserRole.BUYER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_BUYER")));
    }

    @Test
    void testLoadUserByUsernameWithSellerRole() {
        testUser.setUserRole(UserRole.SELLER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_SELLER")));
    }

    @Test
    void testLoadUserByUsernameWithAdminRole() {
        testUser.setUserRole(UserRole.ADMIN);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}
