package com.example.bookxchange.controller;

import com.example.bookxchange.BaseIntegrationTest;
import com.example.bookxchange.dto.UserDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private AuthService authService;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .uid("buyer123")
                .username("buyer1")
                .userEmail("buyer@example.com")
                .userRole(UserRole.BUYER)
                .dateJoined(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "buyer1")
    void dashboard_AsBuyer_ReturnsBuyerDashboard() throws Exception {
        when(authService.getUserDTOByUsername("buyer1")).thenReturn(testUserDTO);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/buyer-dashboard"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "seller1")
    void dashboard_AsSeller_ReturnsSellerDashboard() throws Exception {
        UserDTO sellerDTO = UserDTO.builder()
                .uid("seller123")
                .username("seller1")
                .userEmail("seller@example.com")
                .userRole(UserRole.SELLER)
                .dateJoined(LocalDateTime.now())
                .build();

        when(authService.getUserDTOByUsername("seller1")).thenReturn(sellerDTO);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/seller-dashboard"));
    }

    @Test
    @WithMockUser(username = "admin1")
    void dashboard_AsAdmin_ReturnsAdminDashboard() throws Exception {
        UserDTO adminDTO = UserDTO.builder()
                .uid("admin123")
                .username("admin1")
                .userEmail("admin@example.com")
                .userRole(UserRole.ADMIN)
                .dateJoined(LocalDateTime.now())
                .build();

        when(authService.getUserDTOByUsername("admin1")).thenReturn(adminDTO);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/admin-dashboard"));
    }

    @Test
    @WithMockUser(username = "buyer1")
    void switchRole_ToSeller_Success() throws Exception {
        User testBuyer = User.builder()
                .uid("buyer123")
                .username("buyer1")
                .userEmail("buyer@example.com")
                .passwordHash("hashedPassword123")
                .userRole(UserRole.BUYER)
                .dateJoined(LocalDateTime.now())
                .build();

        when(authService.getUserByUsername("buyer1")).thenReturn(testBuyer);
        when(authService.switchRole("buyer123", UserRole.SELLER)).thenReturn(testBuyer);

        mockMvc.perform(post("/dashboard/switch-role")
                        .param("role", "SELLER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser(username = "seller1")
    void switchRole_ToBuyer_Success() throws Exception {
        User testSeller = User.builder()
                .uid("seller123")
                .username("seller1")
                .userEmail("seller@example.com")
                .passwordHash("hashedPassword123")
                .userRole(UserRole.SELLER)
                .dateJoined(LocalDateTime.now())
                .build();

        when(authService.getUserByUsername("seller1")).thenReturn(testSeller);
        when(authService.switchRole("seller123", UserRole.BUYER)).thenReturn(testSeller);

        mockMvc.perform(post("/dashboard/switch-role")
                        .param("role", "BUYER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void dashboard_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }
}