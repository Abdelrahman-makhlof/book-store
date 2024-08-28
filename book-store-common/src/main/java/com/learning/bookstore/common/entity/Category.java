package com.learning.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="CATEGORIES",uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "CATEGORY")
    private List<Book> books;

}
