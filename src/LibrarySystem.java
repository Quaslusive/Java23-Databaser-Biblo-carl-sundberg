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

        JLabel userLabel = new JLabel("Användarnamn:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Lösenord:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginButton = new JButton("Inloggning");
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            User user = User.login(username, password);
            if (user != null) {
                loggedInUser = user;
                showMainScreen();
            } else {
                JOptionPane.showMessageDialog(frame, "Ogiltigt användarnamn eller lösenord.");
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
        JButton logoutButton = new JButton("Utloggning");
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showLoginScreen();
        });
        JButton updateProfileButton = new JButton("Uppdatera profil");
        updateProfileButton.addActionListener(e -> updateProfile());

        userPanel.add(logoutButton);
        userPanel.add(updateProfileButton);

        // Create a panel for the search field and button
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Söka");
        searchButton.addActionListener(e -> searchBooks());

        searchPanel.add(new JLabel("Söka:"));
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

        JButton loanBookButton = new JButton("Låna Media");
        loanBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) bookTableModel.getValueAt(selectedRow, 0);

                if (Loan.loanBook(loggedInUser.getId(), bookId)) {
                    JOptionPane.showMessageDialog(frame, "Media har utlånats.");
                    bookTableModel.setValueAt("Utlånad", selectedRow, 3);
                } else {
                    JOptionPane.showMessageDialog(frame, "Det gick inte att låna median.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Välj en bok att låna.");
            }
        });

        JButton returnBookButton = new JButton("Returnera media");
        returnBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int bookId = (int) bookTableModel.getValueAt(selectedRow, 0);

                if (Loan.returnBook(loggedInUser.getId(), bookId)) {
                    JOptionPane.showMessageDialog(frame,"Median returnerades framgångsrikt.");
                    bookTableModel.setValueAt("Tillgänglig", selectedRow, 3); // Uppdatera status i tabellen
                } else {
                    JOptionPane.showMessageDialog(frame, "Det gick inte att returnera median. Du kan bara lämna tillbaka media du har lånat.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Välj en media att returnera.");
            }
        });




        JButton historyButton = new JButton("Lånehistorik");
        historyButton.addActionListener(e -> showLoanHistory());


        JButton loanStatusButton = new JButton("Lånestatus");
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

        JLabel nameLabel = new JLabel("Namn:");
        JTextField nameText = new JTextField(loggedInUser.getName());

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailText = new JTextField(loggedInUser.getEmail());

        JLabel passwordLabel = new JLabel("Lösenord:");
        JPasswordField passwordText = new JPasswordField();

        JButton updateButton = new JButton("Uppdatera");
        updateButton.addActionListener(e -> {
            String name = nameText.getText();
            String email = emailText.getText();
            String password = new String(passwordText.getPassword());

            loggedInUser.setName(name);
            loggedInUser.setEmail(email);
            loggedInUser.setPassword(password);

            if (loggedInUser.updateProfile(name, email, password)) {
                JOptionPane.showMessageDialog(frame, "Profilen har uppdaterats. :D");
            } else {
                JOptionPane.showMessageDialog(frame, "Det gick inte att uppdatera profilen. :(");
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

        String[] columnNames = {"Book ID", "titel", "Lånedatum", "Returdatum"};
        DefaultTableModel historyTableModel = new DefaultTableModel(columnNames, 0);
        JTable historyTable = new JTable(historyTableModel);

        for (Loan loan : loans) {
            Book book = Book.getBookById(loan.getBookId());
            historyTableModel.addRow(new Object[]{
                    loan.getBookId(),
                    book != null ? book.getTitle() : "Okänd",
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

        String[] columnNames = {"Book ID", "titel", "Lånedatum", "Returdatum"};
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
