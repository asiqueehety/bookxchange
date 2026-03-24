package com.example.bookxchange.controller;

import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.service.BookService;
import com.example.bookxchange.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PublicController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index() {
        return "landing";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable String id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            BookDTO bookDTO = bookService.convertToDTO(book);

            // Get current logged-in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isOwnBook = false;

            if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
                try {
                    String currentUsername = authentication.getName();
                    User currentUser = authService.getUserByUsername(currentUsername);

                    // Check if the current user is the seller of this book
                    if (currentUser != null && book.getSeller() != null &&
                        currentUser.getUid().equals(book.getSeller().getUid())) {
                        isOwnBook = true;
                    }
                } catch (Exception e) {
                    // If we can't get user info, continue without checking
                }
            }

            model.addAttribute("book", bookDTO);
            model.addAttribute("bookId", id);
            model.addAttribute("isOwnBook", isOwnBook);
            model.addAttribute("sellerId", book.getSeller() != null ? book.getSeller().getUid() : null);
            return "books/book-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Book not found: " + e.getMessage());
            return "landing";
        }
    }
}
