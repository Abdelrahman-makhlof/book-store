package com.learning.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "BOOKS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"TITLE", "AUTHOR"})
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_SEQ_GEN")
    @SequenceGenerator(name = "BOOK_SEQ_GEN", sequenceName = "BOOK_SEQ")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    private double price;
    @Column(name = "RELEASE_DATE")
    private LocalDate releaseDate;
    @ManyToOne
    private Category category;
}
