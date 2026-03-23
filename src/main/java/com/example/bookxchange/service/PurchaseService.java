package com.example.bookxchange.service;

import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.dto.PurchaseDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.CartItem;
import com.example.bookxchange.entity.SoldBook;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.CartItemRepository;
import com.example.bookxchange.repository.SoldBookRepository;
import com.example.bookxchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SoldBookRepository soldBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Validate if purchase is possible
     */
    public boolean validatePurchase(List<CartItemDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        for (CartItemDTO item : items) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found: " + item.getBookId()));

            if (book.getQuantityLeft() < item.getQuantity()) {
                throw new IllegalArgumentException(
                    "Insufficient stock for book: " + book.getBookName() + 
                    ". Available: " + book.getQuantityLeft() + 
                    ", Requested: " + item.getQuantity()
                );
            }
        }

        return true;
    }

    /**
     * Process purchase from cart
     */
    @Transactional
    public PurchaseDTO purchaseBooks(String userId, List<CartItemDTO> items) {
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Validate purchase
        validatePurchase(items);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each item
        for (CartItemDTO item : items) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found: " + item.getBookId()));

            // Decrease stock
            book.setQuantityLeft(book.getQuantityLeft() - item.getQuantity());
            bookRepository.save(book);

            // Record sale
            SoldBook soldBook = SoldBook.builder()
                    .buyer(buyer)
                    .book(book)
                    .build();
            soldBookRepository.save(soldBook);

            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // Return purchase DTO
        return PurchaseDTO.builder()
                .buyerId(buyer.getUid())
                .buyerUsername(buyer.getUsername())
                .items(items)
                .totalItems(items.size())
                .totalAmount(totalAmount)
                .purchaseDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();
    }

    /**
     * Get purchase history for buyer
     */
    public List<PurchaseDTO> getPurchaseHistory(String userId) {
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<SoldBook> purchases = soldBookRepository.findByBuyer(buyer);

        // Group by purchase (for simplicity, we'll treat each sale as a separate purchase)
        // In a full implementation, you'd have a Purchase entity
        return purchases.stream()
                .map(soldBook -> PurchaseDTO.builder()
                        .buyerId(buyer.getUid())
                        .buyerUsername(buyer.getUsername())
                        .totalAmount(soldBook.getBook().getBookPrice())
                        .status("COMPLETED")
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get total purchase amount for buyer
     */
    public BigDecimal getTotalPurchaseAmount(String userId) {
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<SoldBook> purchases = soldBookRepository.findByBuyer(buyer);

        return purchases.stream()
                .map(soldBook -> soldBook.getBook().getBookPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

