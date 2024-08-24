package com.learning.bookstore.controller;

import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.dto.BookDTO;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.request.BookRequest;
import com.learning.bookstore.common.response.BookResponse;
import com.learning.bookstore.common.util.Util;
import com.learning.bookstore.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO, HttpServletRequest httpRequest) {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var bookDto = bookService.saveBook(bookDTO);
        return new ResponseEntity<>(bookDto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<BookDTO> updateBook(RequestEntity<BookDTO> requestEntity) throws ApplicationException {
        var transactionId = requestEntity.getHeaders().get("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId.get(0) : Util.generateTransaction());
        var bookDto = bookService.updateBook(requestEntity.getBody());
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<BookDTO> deleteBook(RequestEntity<BookDTO> requestEntity) {
        var transactionId = requestEntity.getHeaders().get("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId.get(0) : Util.generateTransaction());
        bookService.deleteBook(requestEntity.getBody());
        return new ResponseEntity<>(requestEntity.getBody(), HttpStatus.OK);
    }

    @GetMapping("/{author}")
    public ResponseEntity<BookResponse> getBooksByAuthor(@PathVariable String author) throws ApplicationException {
        var transactionId = Util.generateTransaction();
        Thread.currentThread().setName(transactionId);

        var books = bookService.getBooksByAuthor(author);
        var response = new BookResponse();
        response.setBookList(books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<BookDTO> getBookQueryParam(@RequestParam(required = false) String title, @RequestParam(required = false) String author) throws ApplicationException, ValidationException {
        var transactionId = Util.generateTransaction();
        Thread.currentThread().setName(transactionId);
        BookDTO bookDTO = null;
        if (title == null && author == null)
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        else {
            bookDTO = bookService.getBookByTitleAndAuthor(title, author);
        }

        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @GetMapping("/param")
    public ResponseEntity<BookDTO> getBookQueryParam(RequestEntity requestEntity) throws ApplicationException, ValidationException {
        var transactionId = requestEntity.getHeaders().get("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId.get(0) : Util.generateTransaction());

        var query = requestEntity.getUrl().getQuery();
        var params = Util.parseQueryParams(query);
        var title = params.get("title");
        var author = params.get("author");
        if (title == null && author == null)
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);

        return new ResponseEntity<>(bookService.getBookByTitleAndAuthor(title, author), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<BookResponse> getBooksByRequestBody(RequestEntity<BookRequest> requestEntity) throws ApplicationException {
        var transactionId = requestEntity.getHeaders().get("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId.get(0) : Util.generateTransaction());

        var title = requestEntity.getBody().getTitle();
        var author = requestEntity.getBody().getAuthor();
        var minPrice = requestEntity.getBody().getMinPrice();
        var maxPrice = requestEntity.getBody().getMaxPrice();

        var books = bookService.searchBooks(title, author, minPrice, maxPrice);
        var response = new BookResponse();
        response.setBookList(books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
