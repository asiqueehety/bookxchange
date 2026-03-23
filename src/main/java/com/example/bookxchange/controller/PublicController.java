package com.example.bookxchange.controller;

import com.example.bookxchange.entity.Book;
import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PublicController {

    @Autowired
    private BookService bookService;

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
            model.addAttribute("book", bookDTO);
            model.addAttribute("bookId", id);
            return "books/book-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Book not found: " + e.getMessage());
            return "landing";
        }
    }
}
