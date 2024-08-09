import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
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
        frame.setTitle("Fulköping Biblioteket");

        ImageIcon icon = new ImageIcon("src/asset/icon-image.png");

        frame.setIconImage(icon.getImage());

    }

    private void showLoginScreen() {
        // Rensa alla tidigare komponenter från ramen
        frame.getContentPane().removeAll();

        // Skapa panelen för inloggningsskärmen
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            User user = User.login(username, password);
            if (user != null) {
                loggedInUser = user;
                showMainScreen();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });
        loginPanel.add(userLabel);
        loginPanel.add(userField);
        loginPanel.add(passLabel);
        loginPanel.add(passField);
        loginPanel.add(new JLabel()); // Tom plats för layoutens skull
        loginPanel.add(loginButton);

        // Lägg till loginPanel till ramen
        frame.getContentPane().add(loginPanel);

        // Uppdatera och måla om ramen
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
        String[] columnNames = {"Ordningsnummer", "Titel", "Författare/Skapare", "Media", "Status"};
        bookTableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(bookTableModel);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton loanBookButton = new JButton("Loan Book");
        loanBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) bookTableModel.getValueAt(selectedRow, 0);
                String mediaType = "book"; // Placeholder, you might want to get this dynamically
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



        JButton historyButton = new JButton("Loan History");
        historyButton.addActionListener(e -> showLoanHistory());


        JButton loanStatusButton = new JButton("Loan Status");
        loanStatusButton.addActionListener(e -> showLoanStatus());


        JPanel bottomPanel = new JPanel();
        bottomPanel.add(loanBookButton);
        bottomPanel.add(returnBookButton);
        bottomPanel.add(historyButton);
        bottomPanel.add(loanStatusButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        frame.revalidate();
        frame.repaint();

        Book.loadAllBooks(bookTableModel);
    }


 /*   private void loadAllBooks() {
        List<Book> books = Book.searchBooks(""); // Fetch all books
        updateBookTable(books);
    }*/


    private void searchBooks() {
        String keyword = searchField.getText();
        List<Book> books = Book.searchBooks(keyword);
        updateBookTable(books);
    }

    private void updateBookTable(List<Book> books) {
        bookTableModel.setRowCount(0); // Clear existing rows
        for (Book book : books) {
            String loanStatus = Loan.isBookLoaned(book.getId()) ? "Lånad" : "Tillgänglig";
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

    private void showLoanHistory() {
        List<Loan> loans = Loan.getUserLoans(loggedInUser.getId());

        String[] columnNames = {"Book ID", "Title", "Loan Date", "Return Date"};
        DefaultTableModel historyTableModel = new DefaultTableModel(columnNames, 0);
        JTable historyTable = new JTable(historyTableModel);

        for (Loan loan : loans) {
            Book book = Book.getBookById(loan.getBookId());
            historyTableModel.addRow(new Object[]{
                    loan.getBookId(),
                    book != null ? book.getTitle() : "Unknown",
                    loan.getLoanDate(),
                    loan.getReturnDate()
            });
        }

        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Tillbaka");
        backButton.addActionListener(e -> showMainScreen());
        panel.add(backButton, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        frame.revalidate();
        frame.repaint();
    }
    private void showLoanStatus() {
        List<Loan> loans = Loan.getUserLoans(loggedInUser.getId());

        String[] columnNames = {"Book ID", "Title", "Loan Date", "Due Date"};
        DefaultTableModel statusTableModel = new DefaultTableModel(columnNames, 0);
        JTable statusTable = new JTable(statusTableModel);

        for (Loan loan : loans) {
            if (loan.getReturnDate() == null) { // Only show currently loaned books
                Book book = Book.getBookById(loan.getBookId());
                if (book != null) {

                    LocalDate dueDate = Book.calculateDueDate(loan.getLoanDate(), book.getMedia_type());
                    statusTableModel.addRow(new Object[]{
                            loan.getBookId(),
                            book.getTitle(),
                            loan.getLoanDate(),
                            dueDate

                    });

                } else {
                    statusTableModel.addRow(new Object[]{
                            loan.getBookId(),
                            "okänd",
                            loan.getLoanDate(),
                            "okänd",
                    });
                }

            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(statusTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Tillbaka");
        backButton.addActionListener(e -> showMainScreen());
        panel.add(backButton, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        frame.revalidate();
        frame.repaint();
    }

}
