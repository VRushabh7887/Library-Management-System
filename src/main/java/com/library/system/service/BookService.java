package com.library.system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.system.entity.Book;
import com.library.system.entity.BookTransactionsHistory;
import com.library.system.entity.MyBook;
import com.library.system.repository.BookRepository;
import com.library.system.repository.BookTransactionHistoryRepository;
import com.library.system.repository.MyBookRepository;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private MyBookRepository myBookRepository;
    @Autowired
    private BookTransactionHistoryRepository bookTransactionsHistoryRepository;

    public void save(Book b) {
        bookRepo.save(b);
    }

    public List<Book> getAllBook() {
        return bookRepo.findAll();
    }

    public Book getBookById(int id) {
        return bookRepo.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        List<MyBook> issuedBooks = myBookRepository.findByBookId(id);
        if (!issuedBooks.isEmpty()) {
            throw new RuntimeException("Book is currently issued and cannot be deleted.");
        }

        List<BookTransactionsHistory> historyRecords = bookTransactionsHistoryRepository.findByBookId(id);
        for (BookTransactionsHistory history : historyRecords) {
            history.setBook(null);
            bookTransactionsHistoryRepository.save(history);
        }

        bookRepo.deleteById(id);
    }

    public List<Book> getAllLoanedBooks() {
        return getAllBook().stream()
                .filter(Book::isLoaned)
                .collect(Collectors.toList());
    }

    public Book findByNameAndAuthor(String name, String author) {
        return bookRepo.findByNameAndAuthor(name, author)
                .orElse(null); // Return null if no book is found
    }

    // generate report

}
