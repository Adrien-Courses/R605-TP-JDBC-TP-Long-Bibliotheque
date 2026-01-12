INSERT INTO book (title, author, available) VALUES
('Clean Code', 'Robert C. Martin', TRUE),
('Effective Java', 'Joshua Bloch', TRUE),
('Design Patterns', 'Erich Gamma et al.', TRUE),
('The Pragmatic Programmer', 'Andrew Hunt & David Thomas', TRUE);

-- Example existing loan (optional)
-- INSERT INTO loan (book_id, borrower, loan_date) VALUES (1, 'Alice', CURRENT_DATE());
-- UPDATE book SET available = FALSE WHERE id = 1;
