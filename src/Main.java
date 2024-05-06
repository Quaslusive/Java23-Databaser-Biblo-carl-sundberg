import ui.LibraryManagementSystem;


public class Main {
    public static void main(String[] args) {

        LibraryManagementSystem gui = new LibraryManagementSystem();
        gui.setVisible(true);
        gui.updateBookList();

        System.out.println("Hello world!");
    }
}