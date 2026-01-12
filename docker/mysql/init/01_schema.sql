-- Schema for Library JDBC exercise
CREATE TABLE IF NOT EXISTS book (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS loan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  book_id INT NOT NULL,
  borrower VARCHAR(255) NOT NULL,
  loan_date DATE NOT NULL,
  CONSTRAINT fk_loan_book
    FOREIGN KEY (book_id) REFERENCES book(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE INDEX idx_loan_borrower ON loan(borrower);
CREATE INDEX idx_loan_book ON loan(book_id);
