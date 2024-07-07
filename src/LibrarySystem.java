import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrarySystem {
    private JFrame frame;
    private User loggedInUser;

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
        frame.setBounds(100, 100, 450, 300);
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
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showLoginScreen();
        });

        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(e -> updateProfile());

        JPanel topPanel = new JPanel();
        topPanel.add(logoutButton);
        topPanel.add(updateProfileButton);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create the table to display books
        String[] columnNames = {"ID", "Title", "Author", "Loan Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        List<Book> books = Book.searchBooksByTitle(""); // Fetch all books
        for (Book book : books) {
            String loanStatus = Loan.isBookLoaned(book.getId()) ? "Loaned" : "Available";
            model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), loanStatus});
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton loanBookButton = new JButton("Loan Book");
        loanBookButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) model.getValueAt(selectedRow, 0);
                if (Loan.loanBook(loggedInUser.getId(), bookId)) {
                    JOptionPane.showMessageDialog(frame, "Book loaned successfully.");
                    model.setValueAt("Loaned", selectedRow, 3);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to loan book. It might be already loaned out.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a book to loan.");
            }
        });

        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) model.getValueAt(selectedRow, 0);
                Loan.returnBook(bookId);
                JOptionPane.showMessageDialog(frame, "Book returned successfully.");
                model.setValueAt("Available", selectedRow, 3);
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
