import database.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Loan {
    private int userId;
    private int bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;

    // Constructors
    public Loan() {}

    public Loan(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = LocalDate.now();
    }

    public static boolean loanBook(int userId, int bookId, String media_type) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM loans WHERE book_id = ? AND return_date IS NULL")) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Book is already loaned out
            }

            Book book = Book.getBookById(bookId); // Hämta boken för att få media_type
            LocalDate dueDate = Book.calculateDueDate(LocalDate.now(), book.getMedia_type());

            PreparedStatement loanStmt = conn.prepareStatement("INSERT INTO loans (user_id, book_id, loan_date) VALUES (?, ?, ?)");
            loanStmt.setInt(1, userId);
            loanStmt.setInt(2, bookId);
            loanStmt.setDate(3, Date.valueOf(LocalDate.now()));
            loanStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean returnBook(int userId, int bookId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM loans WHERE book_id = ? AND user_id = ? AND return_date IS NULL")) {
            checkStmt.setInt(1, bookId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Boken är utlånad av den inloggade användaren och kan returneras
                PreparedStatement returnStmt = conn.prepareStatement("UPDATE loans SET return_date = ? WHERE book_id = ? AND user_id = ?");
                returnStmt.setDate(1, Date.valueOf(LocalDate.now()));
                returnStmt.setInt(2, bookId);
                returnStmt.setInt(3, userId);
                returnStmt.executeUpdate();
                return true;
            } else {
                // Boken är antingen inte utlånad av den här användaren eller redan återlämnad
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean isBookLoaned(int bookId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM loans WHERE book_id = ? AND return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Loan> getUserLoans(int userId) {
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM loans WHERE user_id = ? ORDER BY return_date ASC")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setUserId(rs.getInt("user_id"));
                loan.setBookId(rs.getInt("book_id"));
                loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
                loan.setReturnDate(rs.getDate(
                        "return_date") != null ? rs.getDate("return_date").toLocalDate() : null);
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
