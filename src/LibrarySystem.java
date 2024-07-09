import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrarySystem {
    private JFrame frame;
    private User loggedInUser;
    private DefaultTableModel bookTableModel;
    private JTable bookTable;
    private JTextField searchField;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LibrarySystem window = new LibrarySystem();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LibrarySystem() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showLoginScreen();
    }

    private void showLoginScreen() {
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        panel.add(userLabel);

        JTextField userText = new JTextField();
        panel.add(userText);
        userText.setColumns(10);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField();
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            User user = User.login(username, password);
            if (user != null) {
                loggedInUser = user;
                showMainScreen();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });
        panel.add(loginButton);

        frame.revalidate();
        frame.repaint();
    }

    private void showMainScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create a panel for the top controls
        JPanel topPanel = new JPanel(new BorderLayout());

        // Create a panel for the logout and update profile buttons
        JPanel userPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showLoginScreen();
        });
        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(e -> updateProfile());

        userPanel.add(logoutButton);
        userPanel.add(updateProfileButton);

        // Create a panel for the search field and button
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchBooks());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add the userPanel and searchPanel to the topPanel
        topPanel.add(userPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Add the topPanel to the main panel
        panel.add(topPanel, BorderLayout.NORTH);

        // Create the table to display books
        String[] columnNames = {"ID", "Title", "Author", "Loan Status"};
        bookTableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(bookTableModel);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton loanBookButton = new JButton("Loan Book");
        loanBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) bookTableModel.getValueAt(selectedRow, 0);
                if (Loan.loanBook(loggedInUser.getId(), bookId)) {
                    JOptionPane.showMessageDialog(frame, "Book loaned successfully.");
                    bookTableModel.setValueAt("Loaned", selectedRow, 3);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to loan book. It might be already loaned out.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a book to loan.");
            }
        });

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) bookTableModel.getValueAt(selectedRow, 0);
                Loan.returnBook(bookId);
                JOptionPane.showMessageDialog(frame, "Book returned successfully.");
                bookTableModel.setValueAt("Available", selectedRow, 3);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a book to return.");
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(loanBookButton);
        bottomPanel.add(returnBookButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        frame.revalidate();
        frame.repaint();

        loadAllBooks();
    }

    private void loadAllBooks() {
        List<Book> books = Book.searchBooks(""); // Fetch all books
        updateBookTable(books);
    }

    private void searchBooks() {
        String keyword = searchField.getText();
        List<Book> books = Book.searchBooks(keyword);
        updateBookTable(books);
    }

    private void updateBookTable(List<Book> books) {
        bookTableModel.setRowCount(0); // Clear existing rows
        for (Book book : books) {
            String loanStatus = Loan.isBookLoaned(book.getId()) ? "Loaned" : "Available";
            bookTableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), loanStatus});
        }
    }

    private void updateProfile() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameText = new JTextField(loggedInUser.getName());

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailText = new JTextField(loggedInUser.getEmail());

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField();

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            String name = nameText.getText();
            String email = emailText.getText();
            String password = new String(passwordText.getPassword());

            loggedInUser.setName(name);
            loggedInUser.setEmail(email);
            loggedInUser.setPassword(password);

            if (loggedInUser.updateProfile(name, email, password)) {
                JOptionPane.showMessageDialog(frame, "Profile updated successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update profile.");
            }
        });

        JButton backButton = new JButton("Tillbaka");
        backButton.addActionListener(e -> showMainScreen());

        panel.add(nameLabel);
        panel.add(nameText);
        panel.add(emailLabel);
        panel.add(emailText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(updateButton);
        panel.add(backButton);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }
}
