package com.library.system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.system.entity.MyBook;

import com.library.system.repository.MyBookRepository;

@Service
public class MyBookListService {

    @Autowired
    private MyBookRepository mybook;

    public void saveMyBook(MyBook book) {
        mybook.save(book);
    }

    public List<MyBook> getAllMyBooks() {
        return mybook.findAll();
    }

    public void deleteById(int id) {
        mybook.deleteById(id);
    }

    public MyBook getMyBookById(int id) {
        return mybook.findById(id).orElse(null);
    }

    public void deleteMyBook(MyBook myBook) {
        mybook.delete(myBook); // Remove the book from the MyBooks table
    }

    public List<MyBook> getTransactionsByUsername(String username) {
        return mybook.findByLoanedTo(username);
    }

    public MyBook getActiveLoanForUser(int bookId, String username) {
        return mybook.findByBook_IdAndLoanedToAndReturnDateIsNull(bookId, username).orElse(null);
    }

    // to delete book if not issued
    public boolean isBookCurrentlyIssued(int bookId) {
        // Return true if any MyBook record exists for this bookId and not returned yet
        return !mybook.findByBookIdAndReturnDateIsNull(bookId).isEmpty();
    }

}
