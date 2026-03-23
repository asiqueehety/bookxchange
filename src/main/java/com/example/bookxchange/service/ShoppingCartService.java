package com.example.bookxchange.service;

import com.example.bookxchange.dto.CartDTO;
import com.example.bookxchange.dto.CartItemDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.CartItem;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.CartItemRepository;
import com.example.bookxchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Add a book to cart
     */
    public CartItemDTO addToCart(String userId, String bookId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (book.getQuantityLeft() < quantity) {
            throw new IllegalArgumentException("Not enough quantity available. Available: " + book.getQuantityLeft());
        }

        // Check if book already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndBookAndIsActive(user, book, true);

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update quantity
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            cartItem = CartItem.builder()
                    .user(user)
                    .book(book)
                    .quantity(quantity)
                    .isActive(true)
                    .build();
        }

        CartItem savedItem = cartItemRepository.save(cartItem);
        return convertToDTO(savedItem);
    }

    /**
     * Remove book from cart
     */
    public void removeFromCart(String userId, String cartItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getUid().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to cart item");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    /**
     * Update quantity of book in cart
     */
    public CartItemDTO updateCartItemQuantity(String userId, String cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getUid().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to cart item");
        }

        if (cartItem.getBook().getQuantityLeft() < quantity) {
            throw new IllegalArgumentException("Not enough quantity available. Available: " + cartItem.getBook().getQuantityLeft());
        }

        cartItem.setQuantity(quantity);
        CartItem updatedItem = cartItemRepository.save(cartItem);
        return convertToDTO(updatedItem);
    }

    /**
     * Get user's cart
     */
    public CartDTO getCart(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<CartItem> cartItems = cartItemRepository.findByUserAndIsActive(user, true);
        
        List<CartItemDTO> itemDTOs = cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        BigDecimal totalPrice = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .items(itemDTOs)
                .totalItems(itemDTOs.size())
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Clear user's cart
     */
    public void clearCart(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        cartItemRepository.deleteByUserAndIsActive(user, true);
    }

    /**
     * Get cart count
     */
    public Integer getCartItemCount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<CartItem> items = cartItemRepository.findByUserAndIsActive(user, true);
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Convert CartItem entity to DTO
     */
    private CartItemDTO convertToDTO(CartItem cartItem) {
        Book book = cartItem.getBook();
        BigDecimal subtotal = book.getBookPrice().multiply(new BigDecimal(cartItem.getQuantity()));

        return CartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .bookAuthor(book.getBookAuthor())
                .bookPrice(book.getBookPrice())
                .bookCoverPic(book.getBookCoverPic())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}

