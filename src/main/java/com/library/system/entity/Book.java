package com.library.system.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String author;
    private String price;
    private boolean available = true;

    private boolean loaned;
    private LocalDate loanDate;
    // private LocalDate returnDate;
    private String loanedTo;

    private int copiesAvailable;

    // public Book(int id, String name, String author, String price, boolean
    // available) {
    // super();
    // this.id = id;
    // this.name = name;
    // this.author = author;
    // this.price = price;
    // this.available = available;
    // }

    // public Book() {
    // super();
    // // TODO Auto-generated constructor stub
    // }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
