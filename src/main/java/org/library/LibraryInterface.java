package org.library;

import java.util.List;
import java.util.Scanner;
import java.io.PrintWriter;

public class LibraryInterface {
    private static final int ERROR_BOOK_SELECTION = -1;

    public String promptStringInput(Scanner input, PrintWriter output, String prompt){
        displayMsg(output, prompt);
        String inputStr = input.nextLine();

        return inputStr;
    }

    public MenuEnum promptMenu(Scanner input, PrintWriter output){
        // display menu options
        for (MenuEnum o: MenuEnum.values()){
            if (o != MenuEnum.INVALID_INPUT){
                displayMsg(output, o.getFullOptionDesc());
            }
        }
        // get and return user input
        String inputStr = input.nextLine();
        int inputNum = -1;

        try {
            inputNum = Integer.parseInt(inputStr);
        } catch (NumberFormatException e){
            displayMsg(output, "ERROR: invalid input");
        }
        if (inputNum < 1 || inputNum > 3)
            displayMsg(output, "ERROR: invalid input");

        return MenuEnum.getOption(inputNum);
    }

    public int promptBookSelection(Scanner input, PrintWriter output){
        String inputStr = promptStringInput(input, output, "Enter a book number: ");
        int selectionNum;
        try {
            selectionNum = Integer.parseInt(inputStr);

            if (selectionNum < 0) throw new NumberFormatException();
            return selectionNum;
        }catch (NumberFormatException e){
            displayMsg(output, "ERROR: invalid input");
        }
        return ERROR_BOOK_SELECTION;
    }

    public void displayAuthError(AuthEnum error, PrintWriter output){
        if (error == AuthEnum.INVALID_CREDENTIALS){
            displayMsg(output, "ERROR: credentials not found");
        }else if(error == AuthEnum.INVALID_INPUT){
            displayMsg(output, "ERROR: invalid input");
        }
    }
    public void displayAvailableBookNotification(PrintWriter output, Book b){
        String msg = String.format("NOTIFICATION: %s is available!", b.toString());
        displayMsg(output, msg);
    }

    public boolean promptConfirmation(Scanner input, PrintWriter output, String msg){
        if (msg.isBlank()){
            displayMsg(output, "(y/n): ");
        }else{
            String message = String.format("%s (y/n): ", msg);
            displayMsg(output, message);
        }
        String inputStr = input.nextLine();

        return inputStr.equals("y");
    }

    public void displayNumberOfBorrowedBooks(PrintWriter output, int n){
        String msg = String.format("Current number of borrowed books: %d", n);
        displayMsg(output, msg);
    }

    public void notifyNoBorrowedBooks(PrintWriter output){
        displayMsg(output, "You have no borrowed books to return");
    }

    public void notifyMaxBorrowingLimit(PrintWriter output){
        displayMsg(output, "You have reached the maximum borrowing limit and cannot borrow another book");
    }

    public void displayBook(PrintWriter output, Book book, String curUsername){
        displayMsg(output, book.toString());
        displayMsg(output, book.getAvailabilityStatus(curUsername).getDisplayStr());
        displayMsg(output, "due: "+book.getDueDateStr());
    }

    public void displayAllBooks(PrintWriter output, List<Book> books, String username){
        final String divider = "-----------------------";

        displayMsg(output, divider);
        for (int i=0; i<books.size(); ++i){
            displayMsg(output, String.valueOf(i));
            displayBook(output, books.get(i), username);
            displayMsg(output, divider);
        }
    }

    public void displayAlreadyIsHolder(PrintWriter output){
        displayMsg(output, "You already have a hold on this book");
    }

    public void displayAlreadyIsBorrower(PrintWriter output){
        displayMsg(output, "You already have this book checked out");
    }

    public void displayMsg(PrintWriter output, String msg){
        output.println(msg);
        output.flush();
    }

}
