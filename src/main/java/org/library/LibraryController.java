package org.library;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class LibraryController {
    private Library library;
    private LibraryInterface ui;

    LibraryController(Library library, LibraryInterface ui){
        this.library = library;
        this.ui = ui;
    }

    public void promptLogin(Scanner input, PrintWriter output){
        String u = ui.promptStringInput(input, output, "username: ");
        String p = ui.promptStringInput(input, output, "password: ");
        AuthEnum authResult = library.login(u, p);
        ui.displayAuthError(authResult, output);
    }

    public void logout(Scanner input, PrintWriter output){
        boolean typedYes = ui.promptConfirmation(input, output, "Are you sure you want to log out?");

        if (typedYes)
            library.logout();
    }

    public boolean isLoggedIn(){
        return library.getSessionUsername() != null;
    }

    public MenuEnum promptMenu(Scanner input, PrintWriter output){
        return ui.promptMenu(input, output);
    }

    public void displayNumberOfBorrowedBooks(PrintWriter output, int n){
        ui.displayNumberOfBorrowedBooks(output, n);
    }

    public void notifyNoBorrowedBooks(PrintWriter output){
        int borrowedBooksNum = library.getSessionBorrowedBooksNum();
        if (borrowedBooksNum == 0)
            ui.notifyNoBorrowedBooks(output);
    }

    public int promptBorrowBook(Scanner input, PrintWriter output){
        List<Book> books = library.getAllBooks();
        ui.displayAllBooks(output, library.getAllBooks(), library.getSessionUsername());
        return ui.promptBookSelection(input, output);
    }

    public Book getBookByIndex(int i){
        return library.getAllBooks().get(i);
    }

    public void confirmBookCheckOut(Scanner input, PrintWriter output, String bookTitle){
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
        library.updateDueDateFromDate(bookTitle, Calendar.getInstance());

        return true;
    }
}
