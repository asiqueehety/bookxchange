package com.example.bookxchange.service;

import com.example.bookxchange.dto.BookDTO;
import com.example.bookxchange.entity.Book;
import com.example.bookxchange.entity.SoldBook;
import com.example.bookxchange.entity.User;
import com.example.bookxchange.repository.BookRepository;
import com.example.bookxchange.repository.SoldBookRepository;
import com.example.bookxchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SoldBookRepository soldBookRepository;

    /**
     * Create a new book for the seller
     */
    public Book createBook(BookDTO bookDTO, String sellerUsername) {
        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with username: " + sellerUsername));

        Book book = Book.builder()
                .bookName(bookDTO.getBookName())
                .bookCoverPic(bookDTO.getBookCoverPic())
                .bookAuthor(bookDTO.getBookAuthor())
                .bookShortPreview(bookDTO.getBookShortPreview())
                .bookPrice(bookDTO.getBookPrice())
                .quantityLeft(bookDTO.getQuantityLeft())
                .seller(seller)
                .createdAt(LocalDateTime.now())
                .build();

        return bookRepository.save(book);
    }

    /**
     * Update an existing book
     */
    public Book updateBook(String bookId, BookDTO bookDTO, String sellerUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (!book.getSeller().getUsername().equals(sellerUsername)) {
            throw new IllegalArgumentException("You are not authorized to update this book");
        }

        if (bookDTO.getBookName() != null) {
            book.setBookName(bookDTO.getBookName());
        }
        if (bookDTO.getBookCoverPic() != null) {
            book.setBookCoverPic(bookDTO.getBookCoverPic());
        }
        if (bookDTO.getBookAuthor() != null) {
            book.setBookAuthor(bookDTO.getBookAuthor());
        }
        if (bookDTO.getBookShortPreview() != null) {
            book.setBookShortPreview(bookDTO.getBookShortPreview());
        }
        if (bookDTO.getBookPrice() != null) {
            book.setBookPrice(bookDTO.getBookPrice());
        }
        if (bookDTO.getQuantityLeft() != null) {
            book.setQuantityLeft(bookDTO.getQuantityLeft());
        }

        return bookRepository.save(book);
    }

    /**
     * Delete a book
     */
    public void deleteBook(String bookId, String sellerUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (!book.getSeller().getUsername().equals(sellerUsername)) {
            throw new IllegalArgumentException("You are not authorized to delete this book");
        }

        // Delete all related SoldBook records first to avoid foreign key constraint violation
        List<SoldBook> soldBooks = soldBookRepository.findByBook(book);
        if (!soldBooks.isEmpty()) {
            soldBookRepository.deleteAll(soldBooks);
        }

        bookRepository.deleteById(bookId);
    }

    /**
     * Get a book by ID
     */
    public Book getBookById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
    }

    /**
     * Get all books by a specific seller (using username)
     */
    public List<Book> getBooksBySeller(String sellerUsername) {
        return bookRepository.findBySellerUsername(sellerUsername);
    }

    /**
     * Get all available books
     */
    public List<Book> getAllAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getQuantityLeft() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Search books by query and optional filters
     */
    public List<Book> searchBooks(String query, java.util.Map<String, Object> filters) {
        List<Book> books = getAllAvailableBooks();

        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            books = books.stream()
                    .filter(book -> book.getBookName().toLowerCase().contains(lowerQuery)
                            || book.getBookAuthor().toLowerCase().contains(lowerQuery)
                            || book.getBookShortPreview().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }

        if (filters != null) {
            if (filters.containsKey("minPrice")) {
                java.math.BigDecimal minPrice = new java.math.BigDecimal(filters.get("minPrice").toString());
                books = books.stream()
                        .filter(book -> book.getBookPrice().compareTo(minPrice) >= 0)
                        .collect(Collectors.toList());
            }

            if (filters.containsKey("maxPrice")) {
                java.math.BigDecimal maxPrice = new java.math.BigDecimal(filters.get("maxPrice").toString());
                books = books.stream()
                        .filter(book -> book.getBookPrice().compareTo(maxPrice) <= 0)
                        .collect(Collectors.toList());
            }

            if (filters.containsKey("author")) {
                String author = filters.get("author").toString().toLowerCase();
                books = books.stream()
                        .filter(book -> book.getBookAuthor().toLowerCase().contains(author))
                        .collect(Collectors.toList());
            }
        }

        return books;
    }

    /**
     * Convert Book entity to BookDTO
     */
    public BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .bookCoverPic(book.getBookCoverPic())
                .bookAuthor(book.getBookAuthor())
                .bookShortPreview(book.getBookShortPreview())
                .bookPrice(book.getBookPrice())
                .quantityLeft(book.getQuantityLeft())
                .sellerId(book.getSeller().getUid())
                .sellerUsername(book.getSeller().getUsername())
                .createdAt(book.getCreatedAt())
                .build();
    }

    /**
     * Purchase a book (buyer purchases from seller)
     */
    public SoldBook purchaseBook(String bookId, String buyerUsername, Integer quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with username: " + buyerUsername));

        if (book.getQuantityLeft() < quantity) {
            throw new IllegalArgumentException("Not enough quantity available. Available: " + book.getQuantityLeft());
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Decrease book quantity
        book.setQuantityLeft(book.getQuantityLeft() - quantity);
        bookRepository.save(book);

        // Create SoldBook entry for tracking
        SoldBook soldBook = SoldBook.builder()
                .buyer(buyer)
                .book(book)
                .build();

        return soldBookRepository.save(soldBook);
    }

    /**
     * Get all purchased books for a buyer
     */
    public List<SoldBook> getPurchasedBooks(String buyerUsername) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with username: " + buyerUsername));
        
        return soldBookRepository.findByBuyerUid(buyer.getUid());
    }

    /**
     * Get all sold books for a seller (books sold by this seller)
     */
    public List<SoldBook> getSoldBooks(String sellerUsername) {
        return soldBookRepository.findByBookSellerUid(
            userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"))
                .getUid()
        );
    }
}
