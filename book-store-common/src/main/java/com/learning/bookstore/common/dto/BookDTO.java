package com.learning.bookstore.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDTO {

    private Long id;
    @NotNull(message = "Title must not be null")
    private String title;
    @NotNull(message = "Author must not be null")
    private String author;
    private double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private CategoryDTO category;
}
