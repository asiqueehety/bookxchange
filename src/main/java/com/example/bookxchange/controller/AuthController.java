package com.example.bookxchange.controller;

import com.example.bookxchange.dto.RegisterRequest;
import com.example.bookxchange.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(RegisterRequest request, Model model) {
        try {
            authService.register(request);
            model.addAttribute("success", "Registration successful! Please login.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
