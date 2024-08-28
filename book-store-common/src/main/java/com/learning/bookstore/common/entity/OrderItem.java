package com.learning.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ORDER_ITEMS")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_SEQ_GEN")
    @SequenceGenerator(name = "ORDER_SEQ_GEN", sequenceName = "ORDER_SEQ")
    private Long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Order order;
    private int quantity;

}
