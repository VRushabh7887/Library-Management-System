package com.library.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.system.entity.MyBook;

@Repository
public interface MyBookRepository extends JpaRepository<MyBook, Integer> {

    List<MyBook> findByBookId(int bookId);

    List<MyBook> findByLoanedTo(String username);

    Optional<MyBook> findByBook_IdAndLoanedToAndReturnDateIsNull(int bookId, String loanedTo);

    List<MyBook> findByBookIdAndReturnDateIsNull(int bookId);
}
