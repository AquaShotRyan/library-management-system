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
    public static final int BORROWING_LIMIT = 3;

    public Library(){
        InitializeLibrary initLibrary = new InitializeLibrary();
        catalogue = initLibrary.initCatalogue();
        borrowers = initLibrary.initBorrowers();
        borrowTransactions = new ArrayList<>();

        auth = new LibraryAuth(borrowers);
        sessionBorrower = null;
    }

    /* ============ Main Methods ============ */

    /* ------- authentication ------- */
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

    /* ------- borrowing ------- */
    public void checkoutBook(String bookTitle, String username, LibraryDate today){
        setBorrower(bookTitle, username);
        setDueDateFromDate(bookTitle, today);
        addBorrowTransaction(new BorrowTransaction(bookTitle, username, today.toString()));
    }

    public void setBorrower(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        // set user as borrower
        book.setCurBorrower(borrower);

        // remove borrower as current holder if they're the current holder
        if (book.curHolderIs(username)){
            book.popHolder();
        }

        // add book to Borrower's checked-out books
        addBookToBorrower(book, username);
    }

    public void addBookToBorrower(Book book, String username){
        Borrower borrower = borrowers.getBorrower(username);
        if (borrower.hasBook(book.getTitle()))
            throw new IllegalStateException(String.format("%s already has %s checked out", username, book.getTitle()));
        borrower.addBorrowedBook(book);
    }

    public void setDueDateFromDate(String bookTitle, LibraryDate date){
        date.addDays(BORROWING_DAY_LENGTH);

        Book book = getBook(bookTitle);
        book.setDueDate(date);
    }

    public TransactionEnum verifyBorrowing(String bookTitle, String username){
        Book book = getBook(bookTitle);

        TransactionEnum result = TransactionEnum.CAN_BORROW;

        if (isAtBorrowingCapacity(username)){
            result = TransactionEnum.AT_BORROWING_LIMIT;
        }else if (book.hasBorrower()){
            boolean isCurUser = book.curBorrowerIs(username);
            result = isCurUser ? TransactionEnum.CHECKED_OUT_BY_USER : TransactionEnum.CHECKED_OUT_BY_ANOTHER;
        }else if (book.hasHolder()) {
            boolean isCurUser = book.curHolderIs(username);
            result = isCurUser ? result : TransactionEnum.ON_HOLD_BY_ANOTHER;
        }
        return result;
    }

    /* ------- holding ------- */
    public void placeHold(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        book.addHolder(borrower);
        borrower.setCurHold(book.getBookDetails());
    }

    public void addToHoldQueue(String bookTitle, String username){
        Book book = getBook(bookTitle);
        User user = borrowers.getBorrower(username);
        if (book.containsHolder(user)){
            throw new IllegalStateException(String.format("%s already has a hold or is in the queue of %s", username, bookTitle));
        }
        book.addHolder(user);
    }

    public TransactionEnum verifyHolding(String bookTitle, String username){
        Book book = getBook(bookTitle);
        Borrower borrower = borrowers.getBorrower(username);

        if (borrower.hasHold() && !borrower.curHoldIs(bookTitle)){
            return TransactionEnum.AT_HOLD_LIMIT;
        }
        if (book.curHolderIs(username) || book.containsHolder(borrower)){
            return TransactionEnum.ON_HOLD_BY_USER;
        }
        if (!book.hasBorrower() && !book.hasHolder()) {
            return TransactionEnum.BOOK_IS_AVAILABLE;
        }
        return TransactionEnum.CAN_HOLD;
    }

    /* ------- returning ------- */
    public void returnBook(String bookTitle, String username){
        Book book = getBook(bookTitle);

        removeBorrowerFromBook(bookTitle, username);
        removeBookFromBorrower(bookTitle, username);
        book.setDueDate(null);
    }

    public void removeBorrowerFromBook(String bookTitle, String username){
        Book book = getBook(bookTitle);
        if (!book.hasBorrower())
            throw new NullPointerException("No borrower to remove");
        if (!book.curBorrowerIs(username))
            throw new IllegalArgumentException(String.format("Username '%s' doesn't match current borrower", username));

        book.removeCurBorrower();
    }

    public void removeBookFromBorrower(String bookTitle, String username){
        Borrower borrower = borrowers.getBorrower(username);

        if (!borrower.hasBook(bookTitle))
            throw new UnsupportedOperationException(String.format("Book '%s' is not checked out by %s", bookTitle, username));

        borrower.removeBorrowedBook(bookTitle);
    }

    /* ============  Logging Transactions ============  */
    public void addBorrowTransaction(BorrowTransaction b){
        borrowTransactions.add(b);
    }

    public BorrowTransaction getBorrowTransaction(int index){
        return borrowTransactions.get(index);
    }

    public int getBorrowerTransactionsSize(){
        return borrowTransactions.size();
    }

    /* ============  Getters / Setters ============  */
    public Book getBook(String title){
        return catalogue.getBook(title);
    }

    public List<Book> getAllBooks(){
        List<Book> books = catalogue.getAllBooks();
        books.sort(new BookAuthorComparator());
        return books;
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

    public Book getHeldBook(String username){
        Borrower borrower = borrowers.getBorrower(username);

        return getBook(borrower.getCurHold().getTitle());
    }

    public int getBorrowersSize(){
        return borrowers.getBorrowersSize();
    }

    public List<Book> getAllBooksWithAvailability(String username, AvailabilityEnum availability){
        List<Book> allBooks = getAllBooks();
        List<Book> filteredBooks = allBooks.stream().filter(book -> book.getAvailabilityStatus(username) == availability).toList();
        System.out.println(filteredBooks);
        return filteredBooks;
    }

    /* ============ Booleans ============ */
    public boolean borrowerHasBook(String bookTitle, String username){
        return borrowers.getBorrower(username).hasBook(bookTitle);
    }

    public boolean borrowerIsHoldingBook(String bookTitle, String username){
        return borrowers.getBorrower(username).curHoldIs(bookTitle);
    }

    public boolean heldBookIsAvailable(String username){
        Borrower borrower = borrowers.getBorrower(username);
        if (!borrower.hasHold())
            return false;
        Book heldBook = getBook(borrower.getCurHold().getTitle());
        return heldBook.getAvailabilityStatus(username) == AvailabilityEnum.AVAILABLE;
    }

    public boolean canReturnBooks(String username){
        return getBorrowedBooksNum(username) > 0;
    }

    public boolean userIsInHoldQueue(String bookTitle, String username){
        Book book = getBook(bookTitle);
        User user = borrowers.getBorrower(username);
        return book.containsHolder(user);
    }

    public boolean isAtBorrowingCapacity(String username){
        return getBorrowedBooksNum(username) >= BORROWING_LIMIT;
    }
}