package org.library;

import java.util.Calendar;
import java.util.List;

public class Library {
    private Catalogue catalogue;
    private Borrowers borrowers;

    private LibraryAuth auth;
    private Borrower sessionBorrower;

    public static final int BORROWING_DAY_LENGTH = 14;

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
        if (borrower.hasBook(book.getTitle()))
            throw new IllegalStateException(String.format("%s already has %s checked out", username, book.getTitle()));
        borrower.addBorrowedBook(book);
    }

    public BorrowedBooks getBorrowedBooks(String username){
        Borrower borrower = borrowers.getBorrower(username);
        return borrower.getBorrowedBooks();
    }

    public int getBorrowedBooksNum(String username){
        Borrower borrower = borrowers.getBorrower(username);
        return borrower.getBorrowedBooksNum();
    }

    public int getSessionBorrowedBooksNum(){
        return getBorrowedBooksNum(getSessionUsername());
    }

    public void updateDueDateFromDate(String bookTitle, Calendar date){
        date.add(Calendar.DATE, BORROWING_DAY_LENGTH);

        Book book = getBook(bookTitle);
        book.setDueDate(date);
    }

    public void removeBookFromBorrower(String bookTitle, String username){
        Borrower borrower = borrowers.getBorrower(username);

        if (!borrower.hasBook(bookTitle)){
            throw new UnsupportedOperationException(String.format("Book '%s' is not checked out by %s", bookTitle, username));
        }

        borrower.removeBorrowedBook(bookTitle);
    }

    public void placeHold(String bookTitle, String username){
        Book book = getBook(bookTitle);
        User user = borrowers.getBorrower(username);
        if (book.containsHolder(user)){
            throw new IllegalStateException(String.format("%s already has a hold or is in the queue of %s", username, bookTitle));
        }
        book.addHolder(user);
    }

    public void setHolder(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        book.setCurHolder(borrower);
        User firstInQueue = book.getFirstHolder();
        if (firstInQueue != null && firstInQueue.getUsername().equals(username)){
            book.popHolder();
        }
        borrower.setCurHold(book.getBookDetails());
    }

    public Book getHeldBook(String username){
        Borrower borrower = borrowers.getBorrower(username);

        return getBook(borrower.getCurHold().getTitle());
    }

    public void setBorrower(String bookTitle, String username){
        return;
    }
}