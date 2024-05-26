import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseManager;

public class Book {
    private int id;
    private String title;
    private String author;
    private String type;

    public static List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.id = rs.getInt("id");
                book.title = rs.getString("title");
                book.author = rs.getString("author");
                book.type = rs.getString("type");
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static Book getBookById(int id) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Book book = new Book();
                book.id = rs.getInt("id");
                book.title = rs.getString("title");
                book.author = rs.getString("author");
                book.type = rs.getString("type");
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }
}
