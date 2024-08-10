import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseManager;
import javax.swing.table.DefaultTableModel;

public class Book {
    private int id;
    private String title;
    private String author;
    private String media_type;


    public static void loadAllBooks(DefaultTableModel bookTableModel) {
            bookTableModel.setRowCount(0); // Clear the table
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT id, title, author, media_type FROM books ORDER BY title ASC")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("title");
                    row[2] = rs.getString("author");
                    row[3] = rs.getString("media_type");
                    row[4] = Loan.isBookLoaned(rs.getInt("id")) ? "Lånad" : "Tillgänglig";
                    bookTableModel.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    public static List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query =
                "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR media_type LIKE ? ORDER BY media_type ASC ";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setMedia_type(rs.getString("media_type"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static Book getBookById(int id) {
        Book book = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setMedia_type(rs.getString("media_type"));
                // Set other fields as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    // Helper method to calculate due date
    public static LocalDate calculateDueDate(LocalDate loanDate, String media_type) {
        if ("Tidning".equals(media_type) || "Video".equals(media_type) || "TV-spel".equals(media_type)
                || "annat".equals(media_type)) {
            return loanDate.plusDays(10);
        } else {
            return loanDate.plusDays(30);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }
}
