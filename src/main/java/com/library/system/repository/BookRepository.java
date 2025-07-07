package com.library.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.system.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByLoaned(boolean b);

    // to search books by its title or author
    List<Book> findByName(String name);

    List<Book> findByAuthor(String author);

    Optional<Book> findByNameAndAuthor(String name, String author);

}
