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

    public void displayNumberOfBorrowedBooks(){
        ui.displayNumberOfBorrowedBooks(output, library.getSessionBorrowedBooksNum());
    }

    public void notifyNoBorrowedBooks(){
        int borrowedBooksNum = library.getSessionBorrowedBooksNum();
        if (borrowedBooksNum == 0)
            ui.notifyNoBorrowedBooks(output);
    }

    public Book promptBorrowBook(){
        List<Book> books = library.getAllBooks();
        ui.displayAllBooks(output, library.getAllBooks(), library.getSessionUsername());
        try{
            return books.get(ui.promptBookSelection(input, output));
        }catch(IndexOutOfBoundsException e){
            ui.displayMsg(output, "ERROR: invalid input");
        }

        return null;
    }

    public Book promptReturnBook(String username){
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

    public void confirmBookCheckOut(String bookTitle){
        String dueDate = library.getBook(bookTitle).getDueDateStr();
        ui.promptAcknowledgement(input, output, String.format("'%s' has been borrowed and is due on %s", bookTitle, dueDate));
    }

    public void confirmBookReturn(String bookTitle){
        ui.promptAcknowledgement(input, output, String.format("'%s' has been returned", bookTitle));
    }

    public void attemptHold(String bookTitle, String username){
        TransactionEnum holdValidation = library.verifyHolding(bookTitle, username);

        if (holdValidation == TransactionEnum.ON_HOLD_BY_USER){
            ui.displayAlreadyIsHolder(output);
        }else if(holdValidation == TransactionEnum.AT_HOLD_LIMIT){
            ui.displayMsg(output, "You already have another book on hold");
        }else{
            library.setHolder(bookTitle, username);
            ui.promptAcknowledgement(input, output, "You have placed a hold on "+bookTitle);
        }
    }

    public void run(){
        while (true){
            while (!isLoggedIn()){
                promptLogin();
            }
            String sessionUsername = library.getSessionUsername();

            MenuEnum menuInput = promptMenu();

            if (menuInput == MenuEnum.BORROW){
                displayNumberOfBorrowedBooks();

                // display books for user to select to borrow
                Book selectedBook = promptBorrowBook();
                if (selectedBook == null) continue;

                String selectedBookTitle = selectedBook.getTitle();

                // verify if book can be checked out
                TransactionEnum borrowValidation = library.verifyBorrowing(selectedBookTitle, sessionUsername);

                if (borrowValidation == TransactionEnum.CAN_BORROW){
                    // borrow the book and record the transaction
                    library.setBorrower(selectedBookTitle, sessionUsername);
                    LibraryDate today = new LibraryDate(Calendar.getInstance());
                    library.updateDueDateFromDate(selectedBookTitle, today);
                    library.addBorrowTransaction(new BorrowTransaction(selectedBookTitle, sessionUsername, today.toString()));

                    confirmBookCheckOut(selectedBookTitle);
                }else if(borrowValidation == TransactionEnum.CHECKED_OUT_BY_USER){
                    ui.displayAlreadyIsBorrower(output);
                }else{
                    // ask user if they want to place a hold
                    if (!ui.promptOfferHold(input, output, borrowValidation))
                        continue;

                    attemptHold(selectedBookTitle, sessionUsername);
                }
            }else if(menuInput == MenuEnum.RETURN){
                // display notification if user has no borrowed books and return to functionality
                if (library.getSessionBorrowedBooksNum() == 0){
                    notifyNoBorrowedBooks();
                    continue;
                }

                // display borrower's books for user to select to return
                Book selectedBook = promptReturnBook(sessionUsername);

                String selectedBookTitle = selectedBook.getTitle();

                // return book
                library.removeBorrower(selectedBookTitle, sessionUsername);

                // provide return confirmation
                confirmBookReturn(selectedBookTitle);
            }else if (menuInput == MenuEnum.LOGOUT){
                logout();
            }
        }
    }
}
