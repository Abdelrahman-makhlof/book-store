package com.learning.bookstore.bookservice.response;

import com.learning.bookstore.bookservice.dto.BookDTO;
import lombok.Data;

import java.util.List;

@Data
public class BookResponse {

    private List<BookDTO> bookList;
}
