/*
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseManager;

public class Reservation {
    private int userId;
    private int bookId;
    private LocalDate reservationDate;
    private LocalDate expirationDate;

    // Constructors, getters, and setters

    public static boolean reserveBook(int userId, int bookId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM reservations WHERE book_id = ? AND expiration_date > ?")) {
            checkStmt.setInt(1, bookId);
            checkStmt.setDate(2, Date.valueOf(LocalDate.now()));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Book is already reserved
            }

            PreparedStatement reserveStmt = conn.prepareStatement("INSERT INTO reservations (user_id, book_id, reservation_date, expiration_date) VALUES (?, ?, ?, ?)");
            reserveStmt.setInt(1, userId);
            reserveStmt.setInt(2, bookId);
            reserveStmt.setDate(3, Date.valueOf(LocalDate.now()));
            reserveStmt.setDate(4, Date.valueOf(LocalDate.now().plusDays(30)));
            reserveStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Additional methods for managing reservations if needed
}

 */
