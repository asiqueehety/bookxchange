package com.example.bookxchange.controller;

import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private AuthService authService;

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            var userDTO = authService.getUserDTOByUsername(username);
            model.addAttribute("user", userDTO);

            if (userDTO.getUserRole() == UserRole.BUYER) {
                return "dashboard/buyer-dashboard";
            } else if (userDTO.getUserRole() == UserRole.SELLER) {
                return "dashboard/seller-dashboard";
            } else if (userDTO.getUserRole() == UserRole.ADMIN) {
                return "dashboard/admin-dashboard";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading user data");
        }

        return "redirect:/";
    }

    @PostMapping("/switch-role")
    public String switchRole(@RequestParam UserRole role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            var user = authService.getUserByUsername(username);
            authService.switchRole(user.getUid(), role);
        } catch (Exception e) {
            // Handle error
        }

        return "redirect:/dashboard";
    }
}
