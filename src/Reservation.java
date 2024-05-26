import database.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate reservedDate;

    public Reservation(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.reservedDate = LocalDate.now();
    }

    public static boolean reserveBook(int userId, int bookId) {
        if (Loan.isBookLoaned(bookId) && !isBookReserved(bookId)) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String query = "INSERT INTO reservations (user_id, book_id, reserved_date) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isBookReserved(int bookId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM reservations WHERE book_id = ? AND reserved_date > ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookId);
            stmt.setDate(2, Date.valueOf(LocalDate.now().minusDays(30)));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeExpiredReservations() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "DELETE FROM reservations WHERE reserved_date < ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, Date.valueOf(LocalDate.now().minusDays(30)));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Reservation> getUserReservations(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM reservations WHERE user_id = ? AND reserved_date > ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(LocalDate.now().minusDays(30)));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation(userId, rs.getInt("book_id"));
                reservation.id = rs.getInt("id");
                reservation.reservedDate = rs.getDate("reserved_date").toLocalDate();
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
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

    public LocalDate getReservedDate() {
        return reservedDate;
    }
}
