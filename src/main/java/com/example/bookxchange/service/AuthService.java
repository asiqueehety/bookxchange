package com.example.bookxchange.service;

import com.example.bookxchange.dto.LoginRequest;
import com.example.bookxchange.dto.RegisterRequest;
import com.example.bookxchange.dto.UserDTO;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.entity.UserRole;
import com.example.bookxchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) throws Exception {
        // Validate input
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username already exists");
        }

        if (userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new Exception("Email already exists");
        }

        // Create new user - default role is BUYER
        User user = User.builder()
            .username(request.getUsername())
            .userEmail(request.getUserEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .userRole(UserRole.BUYER)
            .build();

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));
    }

    public UserDTO getUserDTOByUsername(String username) throws Exception {
        User user = getUserByUsername(username);
        return UserDTO.builder()
            .uid(user.getUid())
            .username(user.getUsername())
            .userEmail(user.getUserEmail())
            .profilePic(user.getProfilePic())
            .userRole(user.getUserRole())
            .dateJoined(user.getDateJoined())
            .build();
    }

    @Transactional
    public User switchRole(String userId, UserRole newRole) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));

        user.setUserRole(newRole);
        return userRepository.save(user);
    }
}
