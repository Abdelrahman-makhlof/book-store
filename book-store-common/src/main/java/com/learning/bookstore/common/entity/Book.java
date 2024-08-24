package com.learning.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "book", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "author"})
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    private double price;
    private LocalDate releaseDate;
}
