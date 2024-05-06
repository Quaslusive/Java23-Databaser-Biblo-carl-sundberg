package ui;

import database.MySQLConnector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibraryManagementSystem extends JFrame {
    private JTextField titleField, authorField, searchField, userIdField;
    private JButton addButton, searchButton, borrowButton, returnButton;
    private JTextArea bookListArea;
    private Connection connection;

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        try {
            connection = MySQLConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());


        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search Book");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBook();
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel actionPanel = new JPanel(new FlowLayout());
        borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                borrowBook();
            }
        });
        returnButton = new JButton("Return Book");
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
        actionPanel.add(borrowButton);
        actionPanel.add(returnButton);

        bookListArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(bookListArea);


        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.EAST);

        add(mainPanel);
    }
/*
    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        try {
            String query = "INSERT INTO books (title, author) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, author);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Book added successfully.");
                titleField.setText("");
                authorField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 */

    private void searchBook() {
        String searchTerm = searchField.getText().trim();
        try {
            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int publicationYear = resultSet.getInt("publication_year");
                String status = resultSet.getString("status");
                result.append("Title: ").append(title).append(", Author: ").append(author)
                        .append(", Publication Year: ").append(publicationYear)
                        .append(", Status: ").append(status).append("\n");


            }
            if (result.length() > 0) {
                bookListArea.setText(result.toString());
            } else {
                bookListArea.setText("No matching books found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to search books.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println(searchTerm);
    }

    private void borrowBook() {
        String title = searchField.getText().trim();
        int userId = Integer.parseInt(userIdField.getText().trim()); // Assuming you have a field for user ID
        try {
            String query = "INSERT INTO borrowed_books (user_id, book_title) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, title);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Book borrowed successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to borrow book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        String title = searchField.getText().trim();
        int userId = Integer.parseInt(userIdField.getText().trim()); // Assuming you have a field for user ID
        try {
            String query = "DELETE FROM borrowed_books WHERE user_id = ? AND book_title = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, title);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Book returned successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to return book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateBookList() {
        try {
            String query = "SELECT * FROM books";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder bookList = new StringBuilder();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                bookList.append("Title: ").append(title).append(", Author: ").append(author).append("\n");
            }
            bookListArea.setText(bookList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch books.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
