package com.library.system.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookTransactionsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = true) // recent change to ture - 10/06
    private Book book;

    private String bookName;
    private String bookAuthor;
    private String loanedTo;
    private LocalDate loanDate;
    private LocalDate returnDate;
}
