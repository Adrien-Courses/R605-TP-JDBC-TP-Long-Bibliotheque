package fr.adriencaubel.library.dao;

import fr.adriencaubel.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    int create(Book book);
    Optional<Book> findById(int id);
    List<Book> findAll();
    List<Book> findAvailable();
    void update(Book book);
    void setAvailability(int bookId, boolean available);
    List<Book> findAllPagined(int page, int pageSize);
}
