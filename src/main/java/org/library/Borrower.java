package org.library;

public class Borrower extends User{

    BorrowedBooks borrowedBooks;

    public Borrower(String username, String password){
        super(username, password);
        borrowedBooks = new BorrowedBooks();
    }

    public BorrowedBooks getBorrowedBooks() {
        return borrowedBooks;
    }
}
