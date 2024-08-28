package com.learning.bookstore.bookservice.controller;

import com.learning.bookstore.bookservice.request.BookRequest;
import com.learning.bookstore.bookservice.response.BookResponse;
import com.learning.bookstore.bookservice.service.BookService;
import com.learning.bookstore.common.constants.Constants;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.constants.LogKeys;
import com.learning.bookstore.common.dto.BookDTO;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ParsingException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.logger.ApplicationLogger;
import com.learning.bookstore.common.logger.ErrorLogger;
import com.learning.bookstore.common.logger.LoggerFactory;
import com.learning.bookstore.common.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO, HttpServletRequest httpRequest) throws ApplicationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId);

        ApplicationLogger.info("Start to add book");
        ApplicationLogger.info(LoggerFactory.log("Adding new book")
                .put(LogKeys.BOOK, bookDTO.toString())
                .put(LogKeys.USER, "Test user").build());

        bookDTO = bookService.saveBook(bookDTO);
        ApplicationLogger.info(LoggerFactory.log("Book saved successfully")
                .put(LogKeys.BOOK_ID, bookDTO.getId().toString())
                .build());
        return Util.createResponse(bookDTO, HttpStatus.CREATED, startTime);
    }

    @PutMapping
    public ResponseEntity<BookDTO> updateBook(@RequestParam String id, @Valid @RequestBody BookDTO bookDTO, HttpServletRequest httpRequest) throws ApplicationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId);
        ApplicationLogger.info("Start to update book");
        ApplicationLogger.info(LoggerFactory.log("Update book")
                .put(LogKeys.BOOK_ID, id).put(LogKeys.BOOK, bookDTO.toString()).build());

        var bookDto = bookService.updateBook(id, bookDTO);
        ApplicationLogger.info(LoggerFactory.log("Book updated successfully")
                .put(LogKeys.BOOK_ID, id).build());
        return Util.createResponse(bookDto, HttpStatus.OK, startTime);
    }

    @DeleteMapping
    public ResponseEntity deleteBook(@RequestParam(required = false) String id, @RequestParam(required = false) String title, @RequestParam(required = false) String author, HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId);
        ApplicationLogger.info("Start to delete book");
        ApplicationLogger.info(LoggerFactory.log("Deleting new book")
                .put(LogKeys.BOOK_ID, id).put(LogKeys.BOOK_TITLE, title).put(LogKeys.BOOK_AUTHOR, author).build());
        bookService.deleteBook(id, title, author);
        return Util.createResponse(HttpStatus.OK, startTime);
    }

    @GetMapping("/{author}")
    public ResponseEntity<BookResponse> getBooksByAuthor(@PathVariable String author, HttpServletRequest httpRequest) throws ApplicationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId != null ? transactionId : Util.generateTransaction());
        var books = bookService.getBooksByAuthor(author);
        var response = new BookResponse();
        response.setBookList(books);
        return Util.createResponse(response, HttpStatus.OK, startTime);
    }

    @GetMapping
    public ResponseEntity<BookDTO> getBooksByAuthorAndTitle(@RequestParam(required = false) String title, @RequestParam(required = false) String author,
                                                            HttpServletRequest httpRequest) throws ApplicationException, ValidationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId);
        BookDTO bookDTO;
        if (title == null && author == null) {
            ErrorLogger.error("Mandatory parameter is missing");
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        } else {
            bookDTO = bookService.getBookByTitleAndAuthor(title, author);
        }

        return Util.createResponse(bookDTO, HttpStatus.OK, startTime);
    }

    @GetMapping("/entity")
    public ResponseEntity<BookDTO> getBookQueryParam(RequestEntity requestEntity) throws ApplicationException, ValidationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(requestEntity);
        Thread.currentThread().setName(transactionId);

        var query = requestEntity.getUrl().getQuery();
        var params = Util.parseQueryParams(query);
        var title = params.get("title");
        var author = params.get("author");
        if (title == null && author == null) {
            ErrorLogger.error("Mandatory parameter is missing");
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        }
        var book = bookService.getBookByTitleAndAuthor(title, author);
        return Util.createResponse(book, HttpStatus.OK, startTime);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> getBooksByRequestBody(@RequestBody BookRequest bookRequest,
                                                               @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                               HttpServletRequest httpRequest) throws ApplicationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId);

        var title = bookRequest.getTitle();
        var author = bookRequest.getAuthor();
        var minPrice = bookRequest.getMinPrice();
        var maxPrice = bookRequest.getMaxPrice();

        var books = bookService.searchBooks(title, author, minPrice, maxPrice, bookRequest.getCategories(), page, size);

        return Util.createResponse(books, HttpStatus.OK, startTime);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> getBooksByCategory(@RequestParam List<String> category,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(httpRequest);
        Thread.currentThread().setName(transactionId);

        var books = bookService.getBooksByCategories(category, page, size);

        return Util.createResponse(books, HttpStatus.OK, startTime);
    }

    @GetMapping("/http")
    public void getBook(HttpServletRequest request, HttpServletResponse response) throws ApplicationException, ValidationException, IOException, ParsingException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(request);
        Thread.currentThread().setName(transactionId);

        var title = request.getParameter("title");
        var author = request.getParameter("author");

        BookDTO bookDTO = null;
        if (title == null && author == null) {
            ErrorLogger.error(LoggerFactory.log("Mandatory parameter is missing")
                    .put(LogKeys.ERROR_CODE, ErrorCodes.MANDATORY_PARAMETER_IS_MISSING).build());
            throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);
        } else {
            bookDTO = bookService.getBookByTitleAndAuthor(title, author);
        }

        response.addHeader(Constants.TRANSACTION_ID, Thread.currentThread().getName());
        response.addHeader(Constants.EXECUTION_TIME, String.valueOf(System.currentTimeMillis() - startTime));
        response.getWriter().write(Util.convertToString(bookDTO));
    }

    @GetMapping("/http/search")
    public ResponseEntity<List<BookDTO>> getBooksByRequestBody(HttpServletRequest request) throws ApplicationException {
        long startTime = System.currentTimeMillis();
        var transactionId = Util.getTransactionId(request);
        Thread.currentThread().setName(transactionId);


        var title = request.getParameter("title");
        var page = request.getParameter("page");
        var size = request.getParameter("size");

        var pageNumber = Integer.parseInt(page != null ? page : "0");
        var pageSize = Integer.parseInt(size != null ? size : "0");

        var books = bookService.getBooksByTitle(title, pageNumber, pageSize);

        return Util.createResponse(books, HttpStatus.OK, startTime);
    }
}
