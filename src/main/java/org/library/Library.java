package org.library;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private Catalogue catalogue;
    private Borrowers borrowers;
    List<BorrowTransaction> borrowTransactions;

    private LibraryAuth auth;
    private Borrower sessionBorrower;

    public static final int BORROWING_DAY_LENGTH = 14;

    public Library(){
        InitializeLibrary initLibrary = new InitializeLibrary();
        catalogue = initLibrary.initCatalogue();
        borrowers = initLibrary.initBorrowers();
        borrowTransactions = new ArrayList<>();

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

    public List<Book> getBorrowedBooksSorted(String username){
        Borrower borrower = borrowers.getBorrower(username);
        List<Book> borrowedBooksList = borrower.getBorrowedBooksList();
        borrowedBooksList.sort(new BookAuthorComparator());
        return borrowedBooksList;
    }

    public int getSessionBorrowedBooksNum(){
        return getBorrowedBooksNum(getSessionUsername());
    }

    public void updateDueDateFromDate(String bookTitle, LibraryDate date){
        date.addDays(BORROWING_DAY_LENGTH);

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

    public void addToHoldQueue(String bookTitle, String username){
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

        if (book.hasHolder()){
            book.addHolder(borrower);
        }else{
            book.setCurHolder(borrower);

            // delete from queue if user was first in queue
            User firstInQueue = book.peekHolderQueue();
            if (firstInQueue != null && firstInQueue.getUsername().equals(username)){
                book.popHolder();
            }
        }
        borrower.setCurHold(book.getBookDetails());
    }

    public Book getHeldBook(String username){
        Borrower borrower = borrowers.getBorrower(username);

        return getBook(borrower.getCurHold().getTitle());
    }

    public void setBorrower(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        // set user as borrower
        book.setCurBorrower(borrower);

        // update curHolder
        // remove borrower as current holder if he was current holder
        if (book.hasHolder() && book.getCurHolder().equals(borrower)){
            book.removeCurHolder();
        }
        // the first in queue gets popped and set as current holder
        User nextHolder = book.peekHolderQueue();
        if (nextHolder != null){
            setHolder(book.getTitle(), nextHolder.getUsername());
        }
        // add book to Borrower's checked-out books
        borrower.addBorrowedBook(book);
    }

    public void removeBorrower(String bookTitle, String username){
        Book book = getBook(bookTitle);
        if (!book.hasBorrower())
            throw new NullPointerException("No borrower to remove");
        if (!book.getCurBorrower().getUsername().equals(username))
            throw new IllegalArgumentException(String.format("Username '%s' doesn't match current borrower", username));
        book.removeCurBorrower();
        book.setDueDate(null);
        borrowers.getBorrower(username).removeBorrowedBook(bookTitle);
    }

    public boolean borrowerHasBook(String bookTitle, String username){
        return borrowers.getBorrower(username).hasBook(bookTitle);
    }

    public TransactionEnum verifyBorrowing(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        TransactionEnum result = TransactionEnum.CAN_BORROW;

        if (book.hasBorrower()){
            boolean isCurUser = book.getCurBorrower().getUsername().equals(username);
            result = isCurUser ? TransactionEnum.CHECKED_OUT_BY_USER : TransactionEnum.CHECKED_OUT_BY_ANOTHER;
        }else if (book.hasHolder()){
            boolean isCurUser = book.getCurHolder().getUsername().equals(username);
            result = isCurUser ? result : TransactionEnum.ON_HOLD_BY_ANOTHER;
        }else if (borrower.getBorrowedBooksNum() >= 3){
            result = TransactionEnum.AT_BORROWING_LIMIT;
        }

        return result;
    }

    public TransactionEnum verifyHolding(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        if (book.hasHolder()){
            boolean isCurUser = book.getCurHolder().getUsername().equals(username);
            boolean isInQueue = book.containsHolder(borrower);
            if (isCurUser || isInQueue) return TransactionEnum.ON_HOLD_BY_USER;
        }
        if (borrower.hasHold()){
            if (!borrower.getCurHold().getTitle().equals(book.getTitle()))
                return TransactionEnum.AT_HOLD_LIMIT;
        }
        return TransactionEnum.CAN_HOLD;
    }

    public void addBorrowTransaction(BorrowTransaction b){
        borrowTransactions.add(b);
    }

    public BorrowTransaction getBorrowTransaction(int index){
        return borrowTransactions.get(index);
    }

    public int getBorrowerTransactionsSize(){
        return borrowTransactions.size();
    }

    public int getBorrowersSize(){
        return borrowers.getBorrowersSize();
    }

    public boolean heldBookIsAvailable(String username){
        Borrower borrower = borrowers.getBorrower(username);
        if (!borrower.hasHold())
            return false;
        Book heldBook = getBook(borrower.getCurHold().getTitle());
        return heldBook.getAvailabilityStatus(username) == AvailabilityEnum.AVAILABLE;
    }
}