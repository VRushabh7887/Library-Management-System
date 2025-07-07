package com.library.system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.system.entity.BookTransactionsHistory;
import com.library.system.repository.BookTransactionHistoryRepository;

@Service
public class BookTransactionHistoryService {

    @Autowired
    private BookTransactionHistoryRepository repository;

    public void saveTransaction(BookTransactionsHistory transaction) {
        System.out.println("Saving transaction: " + transaction);
        repository.save(transaction);
    }

    public List<BookTransactionsHistory> getAllTransactions() {
        return repository.findAll();
    }
}
