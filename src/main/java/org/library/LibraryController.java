package org.library;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class LibraryController {
    private Library library;
    private LibraryInterface ui;
    private Scanner input;
    private PrintWriter output;

    LibraryController(Library library, LibraryInterface ui, Scanner input, PrintWriter output){
        this.library = library;
        this.ui = ui;
        this.input = input;
        this.output = output;
    }

    public void promptLogin(){
        String u = ui.promptStringInput(input, output, "username: ");
        String p = ui.promptStringInput(input, output, "password: ");
        AuthEnum authResult = library.login(u, p);
        ui.displayAuthError(authResult, output);
    }

    public void logout(){
        boolean typedYes = ui.promptConfirmation(input, output, "Are you sure you want to log out?");

        if (typedYes)
            library.logout();
    }

    public boolean isLoggedIn(){
        return library.getSessionUsername() != null;
    }

    public MenuEnum promptMenu(){
        return ui.promptMenu(input, output);
    }

    public void displayNumberOfBorrowedBooks(int n){
        ui.displayNumberOfBorrowedBooks(output, n);
    }

    public void notifyNoBorrowedBooks(){
        int borrowedBooksNum = library.getSessionBorrowedBooksNum();
        if (borrowedBooksNum == 0)
            ui.notifyNoBorrowedBooks(output);
    }

    public int promptBorrowBook(){
        List<Book> books = library.getAllBooks();
        ui.displayAllBooks(output, library.getAllBooks(), library.getSessionUsername());
        return ui.promptBookSelection(input, output);
    }

    public Book getBookByIndex(int i){
        return library.getAllBooks().get(i);
    }

    public void confirmBookCheckOut(String bookTitle){
        String dueDate = library.getBook(bookTitle).getDueDateStr();
        ui.displayMsg(output, String.format("'%s' has been borrowed and is due on %s", bookTitle, dueDate));
        ui.promptConfirmation(input, output, "Acknowledge completion?");
    }

    public boolean checkOut(String bookTitle){
        final String curUser = library.getSessionUsername();

        // verify if book can be checked out
        TransactionEnum borrowValidation = library.verifyBorrowing(bookTitle, curUser);
        if (borrowValidation != TransactionEnum.CAN_BORROW) return false;

        // borrow the book
        library.setBorrower(bookTitle, curUser);
        library.updateDueDateFromDate(bookTitle, new LibraryDate(Calendar.getInstance()));
        library.addBorrowTransaction(new BorrowTransaction(bookTitle, curUser, library.getBook(bookTitle).getDueDateStr()));

        return true;
    }

    public void run(){
        while (true){
            while (!isLoggedIn()){
                promptLogin();
            }
            MenuEnum menuInput = promptMenu();
            if (menuInput == MenuEnum.BORROW){

                int borrowedBooksNum = library.getSessionBorrowedBooksNum();
                displayNumberOfBorrowedBooks(borrowedBooksNum);

                int bookIndex = promptBorrowBook();
                Book selectedBook = getBookByIndex(bookIndex);
                checkOut(selectedBook.getTitle());

            }else if(menuInput == MenuEnum.RETURN){
                notifyNoBorrowedBooks();

            }else if (menuInput == MenuEnum.LOGOUT){
                logout();
            }
        }
    }
}
