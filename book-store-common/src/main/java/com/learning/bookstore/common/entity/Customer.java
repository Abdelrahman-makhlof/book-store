package com.learning.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "CUSTOMERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"EMAIL", "USERNAME"})})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMER_SEQ_GEN")
    @SequenceGenerator(name = "CUSTOMER_SEQ_GEN", sequenceName = "CUSTOMER_SEQ")
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
}
