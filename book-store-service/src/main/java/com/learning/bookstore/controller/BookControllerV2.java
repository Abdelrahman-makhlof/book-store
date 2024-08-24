package com.learning.bookstore.controller;

import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.dto.BookDTO;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ParsingException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.util.Util;
import com.learning.bookstore.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/books")
public class BookControllerV2 {

    private final BookService bookService;

    @GetMapping
    public void getBookQueryParam(HttpServletRequest request, HttpServletResponse response) throws ApplicationException, ValidationException, IOException, ParsingException {
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
