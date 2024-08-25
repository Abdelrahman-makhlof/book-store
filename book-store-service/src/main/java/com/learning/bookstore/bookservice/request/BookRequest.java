package com.learning.bookstore.bookservice.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequest {

    @NotNull(message = "Id should not be null")
    @NotBlank(message = "Id should not be empty")
    private Long id;
    @NotBlank(message = "Title should not be empty")
    private String title;
    @NotBlank(message = "Author should not be empty")
    private String author;
    private double minPrice;
    private double maxPrice;
}
