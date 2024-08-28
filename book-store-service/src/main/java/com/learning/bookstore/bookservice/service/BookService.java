package com.learning.bookstore.bookservice.service;

import com.learning.bookstore.bookservice.repository.BookRepository;
import com.learning.bookstore.common.constants.DatabaseStructure;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.constants.LogKeys;
import com.learning.bookstore.common.dto.BookDTO;
import com.learning.bookstore.common.entity.Book;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.logger.ApplicationLogger;
import com.learning.bookstore.common.logger.ErrorLogger;
import com.learning.bookstore.common.logger.LoggerFactory;
import com.learning.bookstore.common.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private final EntityManager entityManager;


    public BookDTO saveBook(BookDTO bookDTO) throws ApplicationException {

        try {
            bookRepository.save(Util.map(modelMapper, bookDTO, Book.class));
        } catch (DataIntegrityViolationException e) {
            ErrorLogger.error(LoggerFactory.log("Book already exist")
                    .put(LogKeys.BOOK_TITLE, bookDTO.getTitle()).put(LogKeys.BOOK_AUTHOR, bookDTO.getAuthor())
                    .put(LogKeys.ERROR_CODE, ErrorCodes.DATA_ALREADY_EXIST).build());
            throw new ApplicationException("A book with this title already exists.", ErrorCodes.DATA_ALREADY_EXIST);
        }
        return bookDTO;
    }

    public BookDTO updateBook(String id, BookDTO bookDTO) throws ApplicationException {

        try {
            var book = bookRepository.findById(Long.parseLong(id));
            bookRepository.save(Util.map(modelMapper, bookDTO, Book.class));
        } catch (DataIntegrityViolationException e) {
            ErrorLogger.error(LoggerFactory.log("Book already exist")
                    .put(LogKeys.BOOK_TITLE, bookDTO.getTitle()).put(LogKeys.BOOK_AUTHOR, bookDTO.getAuthor())
                    .put(LogKeys.ERROR_CODE, ErrorCodes.DATA_ALREADY_EXIST).build());
            throw new ApplicationException("A book with this title already exists.", ErrorCodes.DATA_ALREADY_EXIST);
        }
        return bookDTO;
    }

    public void deleteBook(String id, String title, String author) {
        if (title != null && author != null) {
            bookRepository.deleteAllByTitleAndAuthor(title, author);
        } else if (id != null) {
            bookRepository.deleteById(Long.parseLong(id));
        }

    }


    public BookDTO getBookByTitleAndAuthor(String title, String author) throws ApplicationException {
        var book = bookRepository.findBookByTitleAndAuthor(title, author);

        if (book == null) {
            ErrorLogger.error(LoggerFactory.log("No data found")
                    .put(LogKeys.BOOK_TITLE, title).put(LogKeys.BOOK_AUTHOR, author)
                    .put(LogKeys.ERROR_CODE, ErrorCodes.NO_DATA_FOUND).build());
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }
        return Util.map(modelMapper, book, BookDTO.class);
    }

    public List<BookDTO> getBooksByTitle(String title, int pageNumber, int pageSize) throws ApplicationException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        var books = bookRepository.findBooksByTitle(title, pageable);

        if (books == null || books.isEmpty()) {
            ErrorLogger.error(LoggerFactory.log("No data found")
                    .put(LogKeys.BOOK_TITLE, title)
                    .put(LogKeys.ERROR_CODE, ErrorCodes.NO_DATA_FOUND).build());
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }

        return books.getContent().stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByAuthor(String author) throws ApplicationException {
        var books = bookRepository.findBooksByAuthor(author);

        if (books == null || books.isEmpty()) {
            ErrorLogger.error(LoggerFactory.log("No data found")
                    .put(LogKeys.BOOK_AUTHOR, author).put(LogKeys.BOOK_AUTHOR, author)
                    .put(LogKeys.ERROR_CODE, ErrorCodes.NO_DATA_FOUND).build());
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }
        return books.stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookDTO> searchBooks(String title, String author, double minPrice, double maxPrice, List<String> categories, int page, int size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);

        // Define the root of the query (the entity being queried)
        Root<Book> bookRoot = criteriaQuery.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            Predicate titlePredicate = criteriaBuilder.equal(bookRoot.get(DatabaseStructure.BOOK.TITLE), title);
            predicates.add(titlePredicate);
        }
        if (author != null) {
            Predicate authorPredicate = criteriaBuilder.equal(bookRoot.get(DatabaseStructure.BOOK.AUTHOR), author);
            predicates.add(authorPredicate);

        }
        if (minPrice != 0) {
            Predicate minPricePredicate = criteriaBuilder.greaterThanOrEqualTo(bookRoot.get(DatabaseStructure.BOOK.PRICE), minPrice);
            predicates.add(minPricePredicate);
        }
        if (maxPrice != 0) {
            Predicate maxPricePredicate = criteriaBuilder.lessThan(bookRoot.get(DatabaseStructure.BOOK.PRICE), maxPrice);
            predicates.add(maxPricePredicate);
        }

        if (categories != null && !categories.isEmpty()) {
            Predicate categoryPredicate = bookRoot.get(DatabaseStructure.BOOK.CATEGORY).in(categories);
            predicates.add(categoryPredicate);
        }
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        ApplicationLogger.info("Search query: " + criteriaQuery.toString());
        // Create the query and execute
        TypedQuery<Book> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(size);
        query.setFirstResult(page * size);
        return query.getResultList().stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByCategories(List<String> category, int page, int size) {

        return bookRepository.findByCategoryIn(category, PageRequest.of(page, size)).stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }
}
