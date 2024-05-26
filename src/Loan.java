import database.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Loan {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public Loan(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = LocalDate.now();
    }

    public static boolean loanBook(int userId, int bookId) {
        if (isBookLoaned(bookId)) {
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO loans (user_id, book_id, loan_date) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void returnBook(int bookId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "UPDATE loans SET return_date = ? WHERE book_id = ? AND return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM loans WHERE user_id = ? AND return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan(userId, rs.getInt("book_id"));
                loan.id = rs.getInt("id");
                loan.loanDate = rs.getDate("loan_date").toLocalDate();
                loan.returnDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null;
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
}
