package fr.adriencaubel.library;

import fr.adriencaubel.library.config.ConnectionFactory;
import fr.adriencaubel.library.dao.BookDao;
import fr.adriencaubel.library.dao.LoanDao;
import fr.adriencaubel.library.dao.jdbc.JdbcBookDao;
import fr.adriencaubel.library.dao.jdbc.JdbcLoanDao;
import fr.adriencaubel.library.model.Book;
import fr.adriencaubel.library.model.Loan;
import fr.adriencaubel.library.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static LibraryService libraryService;

    public static void main(String[] args) throws Exception {
        log.info("Starting Library JDBC demo...");

        try (Connection connection = ConnectionFactory.openConnection()) {
            BookDao bookDao = new JdbcBookDao(connection);
            LoanDao loanDao = new JdbcLoanDao(connection);
            LibraryService service = new LibraryService(connection, bookDao, loanDao);

            System.out.println("\n--- All books ---");
            printBooks(service.listAllBooks());

            System.out.println("\n--- Available books ---");
            printBooks(service.listAvailableBooks());

            // Demo scenario: borrower "Adrien" borrows book id=2
            int bookIdToBorrow = 2;
            String borrower = "Adrien";
            System.out.println("\n>>> Borrowing book id=" + bookIdToBorrow + " for " + borrower);
            service.borrowBook(bookIdToBorrow, borrower);

            System.out.println("\n--- Available books AFTER borrow ---");
            printBooks(service.listAvailableBooks());

            System.out.println("\n--- Loans for " + borrower + " ---");
            printLoans(service.listLoansForBorrower(borrower));
        }

        log.info("Done.");
    }

    private static void printBooks(List<Book> books) {
        for (Book b : books) {
            System.out.printf(" - #%d | %s | %s | available=%s%n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.isAvailable());
        }
    }

    private static void printLoans(List<Loan> loans) {
        for (Loan l : loans) {
            System.out.printf(" - loan #%d | book_id=%d | borrower=%s | date=%s%n",
                    l.getId(), l.getBookId(), l.getBorrower(), l.getLoanDate());
        }
    }
}
