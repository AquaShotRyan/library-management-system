package org.library;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class LibraryController {
    private Library library;
    private LibraryInterface ui;
    private PrintWriter output;

    LibraryController(Library library, LibraryInterface ui, PrintWriter output){
        this.library = library;
        this.ui = ui;
        this.output = output;
    }

    public void promptLogin(Scanner input){
        String u = ui.promptStringInput(input, output, "username: ");
        String p = ui.promptStringInput(input, output, "password: ");
        AuthEnum authResult = library.login(u, p);
        ui.displayAuthError(authResult, output);
    }

    public void logout(Scanner input){
        boolean typedYes = ui.promptConfirmation(input, output, "Are you sure you want to log out?");

        if (typedYes)
            library.logout();
    }

    public boolean isLoggedIn(){
        return library.getSessionUsername() != null;
    }

    public MenuEnum promptMenu(Scanner input){
        return ui.promptMenu(input, output);
    }

    public void displayNumberOfBorrowedBooks(){
        ui.displayNumberOfBorrowedBooks(output, library.getBorrowedBooksNum(library.getSessionUsername()));
    }

    public void notifyNoBorrowedBooks(){
        int borrowedBooksNum = library.getBorrowedBooksNum(library.getSessionUsername());
        if (borrowedBooksNum == 0)
            ui.notifyNoBorrowedBooks(output);
    }

    public Book promptBorrowBook(Scanner input){
        List<Book> books = library.getAllBooks();
        ui.displayAllBooks(output, library.getAllBooks(), library.getSessionUsername());
        try{
            return books.get(ui.promptBookSelection(input, output));
        }catch(IndexOutOfBoundsException e){
            ui.displayMsg(output, "ERROR: invalid input");
        }

        return null;
    }

    public Book promptReturnBook(Scanner input, String username){
        List<Book> booksToReturn = library.getBorrowedBooksSorted(library.getSessionUsername());
        ui.displayAllReturnBooks(output, booksToReturn);

        try{
            return booksToReturn.get(ui.promptBookSelection(input, output));
        }catch(IndexOutOfBoundsException e){
            ui.displayMsg(output, "ERROR: invalid input");
        }
        return null;
    }

    public Book getBookByIndex(int i){
        return library.getAllBooks().get(i);
    }

    public boolean askBookConfirmation(Scanner input, String bookTitle){
        LibraryDate hypotheticalDueDate = new LibraryDate(Calendar.getInstance());
        hypotheticalDueDate.addDays(Library.BORROWING_DAY_LENGTH);
        String msg = String.format("Do you want to borrow %s and return it on %s?", bookTitle, hypotheticalDueDate.toString());
        return ui.promptConfirmation(input, output, msg);
    }

    public void confirmBookCheckOut(Scanner input, String bookTitle){
        String dueDate = library.getBook(bookTitle).getDueDateStr();
        ui.promptAcknowledgement(input, output, String.format("'%s' has been borrowed and is due on %s", bookTitle, dueDate));
    }

    public void confirmBookReturn(Scanner input, String bookTitle){
        ui.promptAcknowledgement(input, output, String.format("'%s' has been returned", bookTitle));
    }

    public void attemptHold(Scanner input, String bookTitle, String username){
        TransactionEnum holdValidation = library.verifyHolding(bookTitle, username);

        if (holdValidation == TransactionEnum.ON_HOLD_BY_USER){
            ui.displayAlreadyIsHolder(output);
        }else if(holdValidation == TransactionEnum.AT_HOLD_LIMIT){
            ui.displayMsg(output, "You already have another book on hold");
        }else{
            library.placeHold(bookTitle, username);
            ui.promptAcknowledgement(input, output, "You have placed a hold on "+bookTitle);
        }
    }

    public void notifyHeldBookAvailability(String username){
        if (library.heldBookIsAvailable(username)){
            ui.displayAvailableBookNotification(output, library.getHeldBook(username));
        }
    }

    public void run(){
        Scanner input = new Scanner(System.in);
        while (true){
            while (!isLoggedIn()){
                promptLogin(input);
            }
            String sessionUsername = library.getSessionUsername();

            notifyHeldBookAvailability(sessionUsername);

            MenuEnum menuInput = promptMenu(input);

            if (menuInput == MenuEnum.BORROW){
                displayNumberOfBorrowedBooks();

                // display books for user to select to borrow
                Book selectedBook = promptBorrowBook(input);
                if (selectedBook == null) continue;

                String selectedBookTitle = selectedBook.getTitle();

                // verify if book can be checked out
                TransactionEnum borrowValidation = library.verifyBorrowing(selectedBookTitle, sessionUsername);

                if (borrowValidation == TransactionEnum.CAN_BORROW){
                    // ask user if they want to borrow this book
                    if (!askBookConfirmation(input, selectedBookTitle))
                        continue;

                    // borrow the book and record the transaction
                    LibraryDate today = new LibraryDate(Calendar.getInstance());
                    library.checkoutBook(selectedBookTitle, sessionUsername, today);

                    confirmBookCheckOut(input, selectedBookTitle);
                }else if(borrowValidation == TransactionEnum.CHECKED_OUT_BY_USER){
                    ui.displayAlreadyIsBorrower(output);
                }else{
                    // ask user if they want to place a hold
                    if (!ui.promptOfferHold(input, output, borrowValidation))
                        continue;

                    attemptHold(input, selectedBookTitle, sessionUsername);
                }
            }else if(menuInput == MenuEnum.RETURN){
                // display notification if user has no borrowed books and return to functionality
                if (!library.canReturnBooks(sessionUsername)){
                    notifyNoBorrowedBooks();
                    continue;
                }

                // display borrower's books for user to select to return
                Book selectedBook = promptReturnBook(input, sessionUsername);
                if (selectedBook == null) continue;

                String selectedBookTitle = selectedBook.getTitle();

                // return book
                library.removeBorrower(selectedBookTitle, sessionUsername);

                // provide return confirmation
                confirmBookReturn(input, selectedBookTitle);
            }else if (menuInput == MenuEnum.LOGOUT){
                logout(input);
            }
        }
    }
}
