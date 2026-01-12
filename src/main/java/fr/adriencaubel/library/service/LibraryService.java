package fr.adriencaubel.library.service;

import fr.adriencaubel.library.dao.BookDao;
import fr.adriencaubel.library.dao.LoanDao;
import fr.adriencaubel.library.model.Book;
import fr.adriencaubel.library.model.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LibraryService {

    private final Connection connection;
    private final BookDao bookDao;
    private final LoanDao loanDao;

    public LibraryService(Connection connection, BookDao bookDao, LoanDao loanDao) {
        this.connection = connection;
        this.bookDao = bookDao;
        this.loanDao = loanDao;
    }

    public List<Book> listAllBooks() {
        return bookDao.findAll();
    }

    public List<Book> listAvailableBooks() {
        return bookDao.findAvailable();
    }

    public List<Loan> listLoansForBorrower(String borrower) {
        return loanDao.findByBorrower(borrower);
    }

    /**
     * Emprunter un livre = (1) vérifier dispo, (2) créer un loan, (3) mettre book.available=false
     * Le tout dans une transaction.
     */
    public void borrowBook(int bookId, String borrower) {
        boolean previousAutoCommit;
        try {
            previousAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to read connection autoCommit", e);
        }

        try {
            connection.setAutoCommit(false);

            Book book = bookDao.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found: id=" + bookId));

            if (!book.isAvailable()) {
                throw new IllegalStateException("Book is not available: id=" + bookId);
            }

            loanDao.create(new Loan(bookId, borrower, LocalDate.now()));
            bookDao.setAvailability(bookId, false);

            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException("Failed to borrow book (transaction rolled back)", e);
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                // If this fails, the connection is likely unhealthy; propagate as runtime.
                throw new RuntimeException("Failed to restore autoCommit", e);
            }
        }
    }

    public void borrowBookGestionConcurrente(int bookId, String borrower) {
        try {
            connection.setAutoCommit(false);

            // Tentative atomique de réservation du livre
            // car l'UPDATE est protégé par verrou exclusif
            String updateSql = """
            UPDATE book
            SET available = false
            WHERE id = ? AND available = true   
            """;

            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, bookId);

                int updatedRows = ps.executeUpdate();
                if (updatedRows == 0) {
                    throw new IllegalStateException("Book already borrowed or does not exist");
                }
            }

            // Création de l'emprunt
            Loan loan = new Loan(bookId, borrower, LocalDate.now());
            loanDao.create(loan);

            // Validation
            connection.commit();

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException("Failed to borrow book (transaction rolled back)", e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to restore autoCommit", e);
            }
        }
    }
}
