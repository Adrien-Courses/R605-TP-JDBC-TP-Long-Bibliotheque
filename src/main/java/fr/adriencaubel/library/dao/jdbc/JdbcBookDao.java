package fr.adriencaubel.library.dao.jdbc;

import fr.adriencaubel.library.dao.BookDao;
import fr.adriencaubel.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookDao implements BookDao {

    private final Connection connection;

    public JdbcBookDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Book book) {
        final String sql = "INSERT INTO book (title, author, available) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setBoolean(3, book.isAvailable());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    book.setId(id);
                    return id;
                }
            }
            throw new SQLException("No generated key returned for book insert.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create book", e);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        final String sql = "SELECT id, title, author, available FROM book WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book by id", e);
        }
    }

    @Override
    public List<Book> findAll() {
        final String sql = "SELECT id, title, author, available FROM book ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Book> books = new ArrayList<>();
            while (rs.next()) books.add(map(rs));
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all books", e);
        }
    }

    @Override
    public List<Book> findAvailable() {
        final String sql = "SELECT id, title, author, available FROM book WHERE available = TRUE ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Book> books = new ArrayList<>();
            while (rs.next()) books.add(map(rs));
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available books", e);
        }
    }

    @Override
    public void update(Book book) {
        final String sql = "UPDATE book SET title = ?, author = ?, available = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setBoolean(3, book.isAvailable());
            ps.setInt(4, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public void setAvailability(int bookId, boolean available) {
        final String sql = "UPDATE book SET available = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set availability", e);
        }
    }

    @Override
    public List<Book> findAllPagined(int page, int pageSize) {
        final String sql = """
        SELECT id, title, author, available
        FROM book
        ORDER BY id
        LIMIT ? OFFSET ?
        """;

        int offset = (page - 1) * pageSize;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<Book> books = new ArrayList<>();
                while (rs.next()) {
                    books.add(map(rs));
                }
                return books;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to paginate books", e);
        }
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getBoolean("available")
        );
    }
}
