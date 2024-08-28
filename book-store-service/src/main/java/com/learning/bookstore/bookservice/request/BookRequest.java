package com.learning.bookstore.bookservice.request;

import com.learning.bookstore.common.dto.CategoryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookRequest {

    private String title;
    private String author;
    private double minPrice;
    private double maxPrice;
    private List<String> categories;
}
