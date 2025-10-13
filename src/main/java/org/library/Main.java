package org.library;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        // setup
        Library library = new Library();
        LibraryInterface ui = new LibraryInterface();
        LibraryController controller = new LibraryController(library, ui);

        Scanner input = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out);

        // main loop
        while (true){
            while (!controller.isLoggedIn()){
                controller.promptLogin(input, output);
            }
            MenuEnum menuInput = controller.promptMenu(input, output);
            if (menuInput == MenuEnum.BORROW){

                int borrowedBooksNum = library.getSessionBorrowedBooksNum();
                controller.displayNumberOfBorrowedBooks(output, borrowedBooksNum);

                int bookIndex = controller.promptBorrowBook(input, output);
                Book selectedBook = controller.getBookByIndex(bookIndex);
                controller.checkOut(selectedBook.getTitle());

            }else if(menuInput == MenuEnum.RETURN){
                controller.notifyNoBorrowedBooks(output);

            }else if (menuInput == MenuEnum.LOGOUT){
                controller.logout(input, output);
            }
        }
    }
}
