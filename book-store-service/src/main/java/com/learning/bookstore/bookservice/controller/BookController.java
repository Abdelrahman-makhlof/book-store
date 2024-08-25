package com.learning.bookstore.bookservice.controller;

import com.learning.bookstore.bookservice.dto.BookDTO;
import com.learning.bookstore.bookservice.request.BookRequest;
import com.learning.bookstore.bookservice.response.BookResponse;
import com.learning.bookstore.bookservice.service.BookService;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ParsingException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO, HttpServletRequest httpRequest) throws ApplicationException {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var bookDto = bookService.saveBook(bookDTO);
        return new ResponseEntity<>(bookDto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<BookDTO> updateBook(@RequestParam String id, @Valid @RequestBody BookDTO bookDTO, HttpServletRequest httpRequest) throws ApplicationException {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        var bookDto = bookService.updateBook(id, bookDTO);
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<BookDTO> deleteBook(BookDTO bookDTO, HttpServletRequest httpRequest) {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        bookService.deleteBook(bookDTO);
        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @GetMapping("/{author}")
    public ResponseEntity<BookResponse> getBooksByAuthor(@PathVariable String author, HttpServletRequest httpRequest) throws ApplicationException {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var books = bookService.getBooksByAuthor(author);
        var response = new BookResponse();
        response.setBookList(books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<BookDTO> getBookQueryParam(@RequestParam(required = false) String title, @RequestParam(required = false) String author,
                                                     HttpServletRequest httpRequest) throws ApplicationException, ValidationException {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
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
    public ResponseEntity<BookResponse> getBooksByRequestBody(@RequestBody BookRequest bookRequest, HttpServletRequest httpRequest) throws ApplicationException {
        var transactionId = httpRequest.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var title = bookRequest.getTitle();
        var author = bookRequest.getAuthor();
        var minPrice = bookRequest.getMinPrice();
        var maxPrice = bookRequest.getMaxPrice();

        var books = bookService.searchBooks(title, author, minPrice, maxPrice);
        var response = new BookResponse();
        response.setBookList(books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/http")
    public void getBook(HttpServletRequest request, HttpServletResponse response) throws ApplicationException, ValidationException, IOException, ParsingException {
        var transactionId = request.getHeader("transaction-id");
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());

        var title = request.getParameter("title");
        var author = request.getParameter("author");
        BookDTO bookDTO = null;
        if (title == null && author == null)
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        else {
            bookDTO = bookService.getBookByTitleAndAuthor(title, author);
        }

        response.getWriter().write(Util.convertToString(bookDTO));
    }
}
