import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class LibrarySystem {
    private JFrame frame;
    private User loggedInUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySystem().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("Library System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        showLoginScreen();

        frame.setVisible(true);
    }

    private void showLoginScreen() {
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        panel.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            User user = new User(username, password);
            if (user.login()) {
                loggedInUser = user;
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login. Please try again.");
            }
        });

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(loginButton);

        frame.revalidate();
        frame.repaint();
    }

    private void showMainMenu() {
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        panel.setLayout(new GridLayout(6, 1));

        JButton searchBooksButton = new JButton("Search Books");
        searchBooksButton.addActionListener(e -> searchBooks());

        JButton loanBookButton = new JButton("Loan a Book");
        loanBookButton.addActionListener(e -> loanBook());

        JButton returnBookButton = new JButton("Return a Book");
        returnBookButton.addActionListener(e -> returnBook());

        JButton viewLoansButton = new JButton("View Loan History");
        viewLoansButton.addActionListener(e -> viewLoans());

        JButton viewStatusButton = new JButton("View Loan Status");
        viewStatusButton.addActionListener(e -> viewStatus());

        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(e -> updateProfile());

        panel.add(searchBooksButton);
        panel.add(loanBookButton);
        panel.add(returnBookButton);
        panel.add(viewLoansButton);
        panel.add(viewStatusButton);
        panel.add(updateProfileButton);

        frame.revalidate();
        frame.repaint();
    }

    private void searchBooks() {
        String title = JOptionPane.showInputDialog(frame, "Enter book title:");
        if (title != null && !title.isEmpty()) {
            List<Book> books = Book.searchBooksByTitle(title);
            StringBuilder message = new StringBuilder("Search Results:\n");
            for (Book book : books) {
                message.append("ID: ").append(book.getId())
                        .append(", Title: ").append(book.getTitle())
                        .append(", Author: ").append(book.getAuthor()).append("\n");
            }
            JOptionPane.showMessageDialog(frame, message.toString());
        }
    }

    private void loanBook() {
        String bookTitle = JOptionPane.showInputDialog(frame, "Enter Book Title to loan:");
        if (bookTitle != null && !bookTitle.isEmpty()) {
            List<Book> books = Book.searchBooksByTitle(bookTitle);
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No books found with the given title.");
                return;
            }

            Book book = books.get(0); // Assuming we take the first book if there are multiple with the same title
            boolean success = Loan.loanBook(loggedInUser.getId(), book.getId());
            if (success) {
                JOptionPane.showMessageDialog(frame, "Book loaned successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to loan the book. It might be already loaned out.");
            }
        }
        }

                private void returnBook () {
                    String bookIdStr = JOptionPane.showInputDialog(frame, "Enter Book ID to return:");
                    if (bookIdStr != null) {
                        int bookId = Integer.parseInt(bookIdStr);
                        Loan.returnBook(bookId);
                        JOptionPane.showMessageDialog(frame, "Book returned successfully.");
                    }
                }

                private void viewLoans () {
                    List<Loan> loans = Loan.getUserLoans(loggedInUser.getId());
                    StringBuilder message = new StringBuilder("Loan History:\n");
                    for (Loan loan : loans) {
                        message.append("Book ID: ").append(loan.getBookId())
                                .append(", Loan Date: ").append(loan.getLoanDate())
                                .append(", Return Date: ").append(loan.getReturnDate()).append("\n");
                    }
                    JOptionPane.showMessageDialog(frame, message.toString());
                }

                private void viewStatus () {
                    List<Loan> loans = Loan.getUserLoans(loggedInUser.getId());
                    StringBuilder message = new StringBuilder("Current Loans:\n");
                    for (Loan loan : loans) {
                        LocalDate dueDate = loan.getLoanDate().plusDays(30);  // Assuming all media has 30 days loan period for simplicity
                        message.append("Book ID: ").append(loan.getBookId())
                                .append(", Loan Date: ").append(loan.getLoanDate())
                                .append(", Due Date: ").append(dueDate).append("\n");
                    }
                    JOptionPane.showMessageDialog(frame, message.toString());
                }

                private void updateProfile () {
                    JPanel panel = new JPanel(new GridLayout(4, 2));

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

                    panel.add(nameLabel);
                    panel.add(nameText);
                    panel.add(emailLabel);
                    panel.add(emailText);
                    panel.add(passwordLabel);
                    panel.add(passwordText);
                    panel.add(updateButton);

                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(panel, BorderLayout.CENTER);

                    frame.revalidate();
                    frame.repaint();
                }
            }