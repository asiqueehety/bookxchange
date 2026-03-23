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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SoldBookService {

    @Autowired
    private SoldBookRepository soldBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Record a sale when buyer purchases a book
     */
    public SoldBook recordSale(String buyerId, String bookId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with id: " + buyerId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        SoldBook soldBook = SoldBook.builder()
                .buyer(buyer)
                .book(book)
                .build();

        return soldBookRepository.save(soldBook);
    }

    /**
     * Get all books purchased by a buyer
     */
    public List<BookDTO> getBuyerPurchases(String buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with id: " + buyerId));

        List<SoldBook> purchases = soldBookRepository.findByBuyer(buyer);

        return purchases.stream()
                .map(this::convertBookToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all books sold by a seller
     */
    public List<SoldBook> getSellerSales(String sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + sellerId));

        List<SoldBook> allSales = soldBookRepository.findAll();

        // Filter sales where the book belongs to the seller
        return allSales.stream()
                .filter(sale -> sale.getBook().getSeller().getUid().equals(sellerId))
                .collect(Collectors.toList());
    }

    /**
     * Get total sales count for seller
     */
    public Integer getSellerSalesCount(String sellerId) {
        return getSellerSales(sellerId).size();
    }

    /**
     * Get total revenue for seller
     */
    public java.math.BigDecimal getSellerRevenue(String sellerId) {
        List<SoldBook> sales = getSellerSales(sellerId);

        return sales.stream()
                .map(sale -> sale.getBook().getBookPrice())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * Get purchase count for buyer
     */
    public Integer getBuyerPurchaseCount(String buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with id: " + buyerId));

        List<SoldBook> purchases = soldBookRepository.findByBuyer(buyer);
        return purchases.size();
    }

    /**
     * Convert Book entity to BookDTO
     */
    private BookDTO convertBookToDTO(SoldBook soldBook) {
        Book book = soldBook.getBook();

        return BookDTO.builder()
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .bookAuthor(book.getBookAuthor())
                .bookPrice(book.getBookPrice())
                .bookCoverPic(book.getBookCoverPic())
                .bookShortPreview(book.getBookShortPreview())
                .quantityLeft(book.getQuantityLeft())
                .build();
    }
}

