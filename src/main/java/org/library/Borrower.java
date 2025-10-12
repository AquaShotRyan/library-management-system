package org.library;

public class Borrower extends User{

    BorrowedBooks borrowedBooks;
    BookDetails curHold;

    public Borrower(String username, String password){
        super(username, password);
        borrowedBooks = new BorrowedBooks();
    }

    public BorrowedBooks getBorrowedBooks() {
        return borrowedBooks;
    }

    public boolean hasBook(String bookTitle){
        return borrowedBooks.getBookByTitle(bookTitle) != null;
    }

    public void addBorrowedBook(Book b){
        borrowedBooks.addBook(b);
    }

    public void removeBorrowedBook(String bookTitle){
        borrowedBooks.removeBook(bookTitle);
    }

    public int getBorrowedBooksNum(){ return borrowedBooks.size(); }

    public void setCurHold(BookDetails book){ curHold = book; }

    public BookDetails getCurHold(){ return curHold; }
}
