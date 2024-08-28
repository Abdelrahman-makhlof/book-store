package com.learning.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_SEQ_GEN")
    @SequenceGenerator(name = "ORDER_SEQ_GEN", sequenceName = "ORDER_SEQ")
    private long id;
    private LocalDateTime orderTime;
    private double total;
    @ManyToOne
    private Customer user;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;


}
