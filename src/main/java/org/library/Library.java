package org.library;

import java.util.List;

public class Library {
    private Catalogue catalogue;
    private Borrowers borrowers;

    private LibraryAuth auth;
    private Borrower sessionBorrower;

    public Library(){
        InitializeLibrary initLibrary = new InitializeLibrary();
        catalogue = initLibrary.initCatalogue();
        borrowers = initLibrary.initBorrowers();

        auth = new LibraryAuth(borrowers);
        sessionBorrower = null;
    }

    public AuthEnum login(String username, String password){
        AuthEnum authResult = auth.authUser(username, password);

        if (authResult == AuthEnum.SUCCESS)
            sessionBorrower = borrowers.getBorrower(username);

        return authResult;
    }

    public void logout(){
        sessionBorrower = null;
    }

    public String getSessionUsername(){
        if (sessionBorrower == null) return null;
        return sessionBorrower.getUsername();
    }

    public Book getBook(String title){
        return catalogue.getBook(title);
    }

    public List<Book> getAllBooks(){
        List<Book> books = catalogue.getAllBooks();
        books.sort(new BookAuthorComparator());
        return books;
    }

    public void addBookToBorrower(Book book, String username){
        Borrower borrower = borrowers.getBorrower(username);
        BorrowedBooks borrowedBooks = borrower.getBorrowedBooks();
        borrowedBooks.addBook(book);
    }

    public BorrowedBooks getBorrowedBooks(String username){
        Borrower borrower = borrowers.getBorrower(username);
        return borrower.getBorrowedBooks();
    }

    public int getBorrowedBooksNum(String username){
        BorrowedBooks borrowedBooks = getBorrowedBooks(username);
        return borrowedBooks.size();
    }

    public int getSessionBorrowedBooksNum(){
        return getBorrowedBooksNum(getSessionUsername());
    }
}