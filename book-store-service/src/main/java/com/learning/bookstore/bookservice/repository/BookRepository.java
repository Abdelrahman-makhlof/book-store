package com.learning.bookstore.bookservice.repository;

import com.learning.bookstore.common.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findBooksByTitle(String title, Pageable pageable);

    List<Book> findBooksByAuthor(String author);

    Page<Book> findByCategoryIn(List<String> categories,Pageable pageable);


    Book findBookByTitleAndAuthor(String title, String author);

    void deleteAllByTitleAndAuthor(String title, String author);
}
