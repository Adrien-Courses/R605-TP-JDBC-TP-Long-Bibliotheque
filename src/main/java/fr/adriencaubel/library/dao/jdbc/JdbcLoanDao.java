package fr.adriencaubel.library.dao.jdbc;

import fr.adriencaubel.library.dao.LoanDao;
import fr.adriencaubel.library.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcLoanDao implements LoanDao {

    private final Connection connection;

    public JdbcLoanDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Loan loan) {
        final String sql = "INSERT INTO loan (book_id, borrower, loan_date) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getBookId());
            ps.setString(2, loan.getBorrower());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    loan.setId(id);
                    return id;
                }
            }
            throw new SQLException("No generated key returned for loan insert.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create loan", e);
        }
    }

    @Override
    public List<Loan> findByBorrower(String borrower) {
        final String sql = "SELECT id, book_id, borrower, loan_date FROM loan WHERE borrower = ? ORDER BY loan_date DESC, id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, borrower);
            try (ResultSet rs = ps.executeQuery()) {
                List<Loan> loans = new ArrayList<>();
                while (rs.next()) loans.add(map(rs));
                return loans;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find loans by borrower", e);
        }
    }

    private Loan map(ResultSet rs) throws SQLException {
        LocalDate date = rs.getDate("loan_date").toLocalDate();
        return new Loan(
                rs.getInt("id"),
                rs.getInt("book_id"),
                rs.getString("borrower"),
                date
        );
    }
}
