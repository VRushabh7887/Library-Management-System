package com.library.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.system.entity.BookTransactionsHistory;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionsHistory, Integer> {

    List<BookTransactionsHistory> findByBookId(int id);

}
