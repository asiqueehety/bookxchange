package com.example.bookxchange.controller;

import com.example.bookxchange.dto.ChangePasswordRequest;
import com.example.bookxchange.dto.UpdateProfileRequest;
import com.example.bookxchange.dto.UserStatsDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.service.AuthService;
import com.example.bookxchange.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * Display user profile page
     */
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        try {
            String userId = getCurrentUserId();
            UserStatsDTO userStats = userService.getUserStats(userId);
            model.addAttribute("userStats", userStats);
            return "user/profile";
        } catch (Exception e) {
            log.error("Error loading profile: {}", e.getMessage());
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            // Redirect to login if user not authenticated
            return "redirect:/login";
        }
    }

    /**
     * Update profile information
     */
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UpdateProfileRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            String userId = getCurrentUserId();
            userService.updateProfile(userId, request);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * Upload profile picture
     */
    @PostMapping("/profile/upload-picture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Please select a file");
            }

            String userId = getCurrentUserId();
            userService.uploadProfilePicture(userId, file);
            redirectAttributes.addFlashAttribute("success", "Profile picture updated successfully!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            log.error("Error uploading profile picture: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * Delete profile picture
     */
    @PostMapping("/profile/delete-picture")
    public String deleteProfilePicture(RedirectAttributes redirectAttributes) {
        try {
            String userId = getCurrentUserId();
            userService.deleteProfilePicture(userId);
            redirectAttributes.addFlashAttribute("success", "Profile picture removed successfully!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            log.error("Error deleting profile picture: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * Change password
     */
    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            String userId = getCurrentUserId();
            userService.changePassword(userId, request);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * Delete account
     */
    @PostMapping("/profile/delete-account")
    public String deleteAccount(RedirectAttributes redirectAttributes) {
        try {
            String userId = getCurrentUserId();
            userService.deleteAccount(userId);
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully!");
            // Logout the user
            SecurityContextHolder.clearContext();
            return "redirect:/";
        } catch (Exception e) {
            log.error("Error deleting account: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    /**
     * Get current user ID from authentication
     * Converts username to actual user UID
     */
    private String getCurrentUserId() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User not authenticated");
        }
        // Get username from authentication
        String username = authentication.getName();
        log.debug("Getting user ID for username: {}", username);

        // Fetch the actual user to get the UUID
        User user = authService.getUserByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        return user.getUid();
    }
}
