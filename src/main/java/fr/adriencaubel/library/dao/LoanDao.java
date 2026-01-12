package fr.adriencaubel.library.dao;

import fr.adriencaubel.library.model.Loan;

import java.util.List;

public interface LoanDao {
    int create(Loan loan);
    List<Loan> findByBorrower(String borrower);
}
