package com.learning.bookstore.common.request;

import lombok.Data;

@Data
public class BookRequest {

    private String title;
    private String author;
    private double minPrice;
    private double maxPrice;
}
