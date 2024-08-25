package com.learning.bookstore.bookservice.service;

import com.learning.bookstore.bookservice.constants.DatabaseStructure;
import com.learning.bookstore.bookservice.dto.BookDTO;
import com.learning.bookstore.bookservice.entity.Book;
import com.learning.bookstore.bookservice.repository.BookRepository;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.exception.ApplicationException;
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
            throw new ApplicationException("A book with this title already exists.", ErrorCodes.DATA_ALREADY_EXIST);
        }
        return bookDTO;
    }

    public BookDTO updateBook(String id, BookDTO bookDTO) throws ApplicationException {

        try {
            var book = bookRepository.findById(Long.parseLong(id));
            bookRepository.save(Util.map(modelMapper, bookDTO, Book.class));
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationException("A book with this title already exists.", ErrorCodes.DATA_ALREADY_EXIST);
        }
        return bookDTO;
    }

    public void deleteBook(BookDTO bookDTO) {

        bookRepository.deleteAllByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor());
    }


    public BookDTO getBookByTitleAndAuthor(String title, String author) throws ApplicationException {
        var book = bookRepository.findBookByTitleAndAuthor(title, author);

        if (book == null) {
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }
        return Util.map(modelMapper, book, BookDTO.class);
    }

    public List<BookDTO> getBooksByTitle(String title) throws ApplicationException {
        var books = bookRepository.findBooksByTitle(title);

        if (books == null || books.isEmpty()) {
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }

        return books.stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByAuthor(String author) throws ApplicationException {
        var books = bookRepository.findBooksByAuthor(author);

        if (books == null || books.isEmpty()) {
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }
        return books.stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookDTO> searchBooks(String title, String author, double minPrice, double maxPrice) {
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

        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }
        // Create the query and execute
        TypedQuery<Book> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList().stream()
                .map(book -> Util.map(modelMapper, book, BookDTO.class))
                .collect(Collectors.toList());
    }
}
