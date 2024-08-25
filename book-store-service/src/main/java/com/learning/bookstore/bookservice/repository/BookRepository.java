package com.learning.bookstore.bookservice.repository;

import com.learning.bookstore.bookservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBooksByTitle(String title);

    List<Book> findBooksByAuthor(String author);

    Book findBookByTitleAndAuthor(String title, String author);

    void deleteAllByTitleAndAuthor(String title, String author);
}
