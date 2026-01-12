package fr.adriencaubel.library.model;

import java.time.LocalDate;

public class Loan {
    private int id;
    private int bookId;
    private String borrower;
    private LocalDate loanDate;

    public Loan() {}

    public Loan(int id, int bookId, String borrower, LocalDate loanDate) {
        this.id = id;
        this.bookId = bookId;
        this.borrower = borrower;
        this.loanDate = loanDate;
    }

    public Loan(int bookId, String borrower, LocalDate loanDate) {
        this.bookId = bookId;
        this.borrower = borrower;
        this.loanDate = loanDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getBorrower() { return borrower; }
    public void setBorrower(String borrower) { this.borrower = borrower; }
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", borrower='" + borrower + '\'' +
                ", loanDate=" + loanDate +
                '}';
    }
}
