package com.example.bookxchange.service;

import com.example.bookxchange.dto.ChangePasswordRequest;
import com.example.bookxchange.dto.UpdateProfileRequest;
import com.example.bookxchange.dto.UserStatsDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.BookRequestRepository;
import com.example.bookxchange.repository.SoldBookRepository;
import com.example.bookxchange.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SoldBookRepository soldBookRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * Get user by ID
     */
    public User getUserById(String userId) throws Exception {
        return userRepository.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
    }

    /**
     * Get user statistics
     */
    public UserStatsDTO getUserStats(String userId) throws Exception {
        User user = getUserById(userId);

        // Count books posted (for sellers)
        long booksPosted = bookRepository.countBySellerId(userId);

        // Count books sold
        long booksSold = soldBookRepository.countBySellerIdFromBooks(userId);

        // Count books purchased
        long booksPurchased = soldBookRepository.countByBuyerId(userId);

        // Count book requests made
        long requestsMade = bookRequestRepository.countByBuyerId(userId);

        return UserStatsDTO.builder()
            .uid(user.getUid())
            .username(user.getUsername())
            .userEmail(user.getUserEmail())
            .profilePic(user.getProfilePic())
            .userRole(user.getUserRole())
            .dateJoined(user.getDateJoined())
            .totalBooksPosted((int) booksPosted)
            .totalBooksSold((int) booksSold)
            .totalBooksPurchased((int) booksPurchased)
            .totalRequestsMade((int) requestsMade)
            .build();
    }

    /**
     * Update user profile information
     */
    @Transactional
    public User updateProfile(String userId, UpdateProfileRequest request) throws Exception {
        User user = getUserById(userId);

        // Check if new username already exists (and it's not the current user)
        if (!user.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username already taken");
        }

        // Check if new email already exists (and it's not the current user)
        if (!user.getUserEmail().equals(request.getUserEmail()) &&
            userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new Exception("Email already in use");
        }

        user.setUsername(request.getUsername());
        user.setUserEmail(request.getUserEmail());

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", userId);
        return updatedUser;
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) throws Exception {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new Exception("New passwords do not match");
        }

        if (request.getNewPassword().length() < 6) {
            throw new Exception("Password must be at least 6 characters");
        }

        User user = getUserById(userId);

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new Exception("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    /**
     * Upload profile picture
     */
    @Transactional
    public String uploadProfilePicture(String userId, MultipartFile file) throws Exception {
        User user = getUserById(userId);

        // Delete old profile picture if exists
        if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
            fileUploadService.deleteFile(user.getProfilePic());
        }

        // Save new profile picture
        String filename = fileUploadService.saveFile(file);
        user.setProfilePic(filename);
        userRepository.save(user);

        log.info("Profile picture uploaded for user: {}", userId);
        return filename;
    }

    /**
     * Delete profile picture
     */
    @Transactional
    public void deleteProfilePicture(String userId) throws Exception {
        User user = getUserById(userId);

        if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
            fileUploadService.deleteFile(user.getProfilePic());
            user.setProfilePic(null);
            userRepository.save(user);
            log.info("Profile picture deleted for user: {}", userId);
        }
    }

    /**
     * Delete user account
     */
    @Transactional
    public void deleteAccount(String userId) throws Exception {
        User user = getUserById(userId);

        // Delete profile picture if exists
        if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
            fileUploadService.deleteFile(user.getProfilePic());
        }

        // Delete user from database
        userRepository.deleteById(userId);
        log.info("User account deleted: {}", userId);
    }
}
