package org.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    Library library;

    @BeforeEach
    void initLibrary(){
        library = new Library();
    }

    @Nested
    @DisplayName("RESP-13: retrieve all books sorted by author")
    public class GetAllBooks {
        List<Book> allBooks;

        @BeforeEach
        void getAllBooks(){
            allBooks = library.getAllBooks();
        }

        @Test
        @DisplayName("Getting all books returns an array of size 20")
        void RESP_13_test_1(){
            int resultSize = allBooks.size();

            assertEquals(20, resultSize);
        }

        @Test
        @DisplayName("Book with author 'Arthur C. Clarke' is at the first index")
        void RESP_13_test_2(){
            try{
                Book book = allBooks.getFirst();
                assertEquals("Arthur C. Clarke", book.getAuthor());
            }catch(NoSuchElementException e) {
                fail("Books collection is empty");
            }
        }

        @Test
        @DisplayName("Book with author 'Tom King is at the last index")
        void RESP_13_test_3(){
            try {
                Book book = allBooks.getLast();
                assertEquals("Tom King", book.getAuthor());
            } catch(NoSuchElementException e) {
                fail("Books collection is empty");
            }
        }
    }

    @Nested
    @DisplayName("RESP-20: add book to a borrower's checked-out books")
    public class AddCheckedOutBook {
        private final String CUR_USER = "squeex";

        @Test
        @DisplayName("Check added book 'Berserk Deluxe Volume 1' is in Borrower's borrowed books")
        void RESP_20_test_1(){
            Book book = library.getBook("Berserk Deluxe Volume 1");
            library.addBookToBorrower(book, CUR_USER);

            BorrowedBooks borrowedBooks = library.getBorrowedBooks(CUR_USER);
            Book borrowedBook = borrowedBooks.getBookByTitle(book.getTitle());

            assertEquals(book.getTitle(), borrowedBook.getTitle());
        }

        @Test
        @DisplayName("Add 3 books and check the 2nd book, 'Moby-Dick,' is in Borrower's borrowed books")
        void RESP_20_test_2(){
            Book book1 = library.getBook("Blood Meridian");
            Book book2 = library.getBook("Moby-Dick");
            Book book3 = library.getBook("Berserk Deluxe Volume 1");
            library.addBookToBorrower(book1, CUR_USER);
            library.addBookToBorrower(book2, CUR_USER);
            library.addBookToBorrower(book3, CUR_USER);

            BorrowedBooks borrowedBooks = library.getBorrowedBooks("squeex");
            Book borrowedBook = borrowedBooks.getBookByTitle(book2.getTitle());

            assertEquals(book2.getTitle(), borrowedBook.getTitle());
        }

        @Test
        @DisplayName("Add 3 books and check size is 3")
        void RESP_20_test_3(){
            Book book1 = library.getBook("Blood Meridian");
            Book book2 = library.getBook("Moby-Dick");
            Book book3 = library.getBook("Berserk Deluxe Volume 1");
            library.addBookToBorrower(book1, "squeex");
            library.addBookToBorrower(book2, "squeex");
            library.addBookToBorrower(book3, "squeex");

            BorrowedBooks borrowedBooks = library.getBorrowedBooks("squeex");

            assertEquals(3, borrowedBooks.size());
        }

        @Test
        @DisplayName("Adding a duplicate book throws IllegalStateException")
        void RESP_20_test_4(){
            Book book1 = library.getBook("Blood Meridian");
            library.addBookToBorrower(book1, "squeex");

            assertThrows(IllegalStateException.class, () -> library.addBookToBorrower(book1, "squeex"));
        }
    }

    @Nested
    @DisplayName("RESP-18: update a book's due date to 14 days from today")
    public class BookDueDate{

        @Test
        @DisplayName("If today is 2025-09-05, then due date is 2025-09-19")
        void RESP_18_test_1(){

            LibraryDate today = new LibraryDate(2025, Calendar.SEPTEMBER, 5);
            LibraryDate expectedDate = new LibraryDate(2025, Calendar.SEPTEMBER, 19);

            Book book = library.getBook("Red Rising");
            library.updateDueDateFromDate(book.getTitle(), today);

            LibraryDate dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }

        @Test
        @DisplayName("If today is 2025-10-24, then due date is 2025-11-07")
        void RESP_18_test_2(){
            LibraryDate today = new LibraryDate(2025, Calendar.OCTOBER, 24);
            LibraryDate expectedDate = new LibraryDate(2025, Calendar.NOVEMBER, 7);

            Book book = library.getBook("Red Rising");
            library.updateDueDateFromDate(book.getTitle(), today);

            LibraryDate dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }

        @Test
        @DisplayName("If today is 2025-12-29, then due date is 2026-01-12")
        void RESP_18_test_3(){
            LibraryDate  today = new LibraryDate (2025, Calendar.DECEMBER, 29);
            LibraryDate  expectedDate = new LibraryDate(2026, Calendar.JANUARY, 12);

            Book book = library.getBook("Red Rising");
            library.updateDueDateFromDate(book.getTitle(), today);

            LibraryDate  dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }


    }

    @Nested
    @DisplayName("RESP-24: remove a book from a borrower's checked-out books")
    public class RemoveCheckedOutBook {
        private final String CUR_USER = "ryan";
        private final String BOOK1 = "Supergirl: Woman of Tomorrow #1";
        private final String BOOK2 = "The Art and Making of Arcane";

        @BeforeEach
        void addBorrowedBooks() {
            Book book1 = library.getBook(BOOK1);
            Book book2 = library.getBook(BOOK2);
            library.setBorrower(BOOK1, CUR_USER);
            library.setBorrower(BOOK2, CUR_USER);
        }

        @Test
        @DisplayName("Remove 1 book from collection of 2, size should be 1")
        void RESP_24_test_1(){
            library.removeBookFromBorrower(BOOK1, CUR_USER);
            int result = library.getBorrowedBooksNum(CUR_USER);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("Remove book 'Supergirl: Woman of Tomorrow #1', finding it should return null")
        void RESP_24_test_2(){
            library.removeBookFromBorrower(BOOK1, CUR_USER);
            Book result = library.getBorrowedBooks(CUR_USER).getBookByTitle(BOOK1);

            assertNull(result);
        }

        @Test
        @DisplayName("Remove book 'Supergirl: Woman of Tomorrow #1', removing it again should throw UnsupportedOperationException")
        void RESP_24_test_3(){
            library.removeBookFromBorrower(BOOK1, CUR_USER);
            assertThrows(UnsupportedOperationException.class, () -> library.removeBookFromBorrower(BOOK1, CUR_USER));
        }
    }

    @Nested
    @DisplayName("RESP-17: add borrower to book's queue")
    public class AddBorrowerToQueue{
        private final String BOOK_TITLE = "Crime and Punishment";
        private final String RYAN = "ryan";
        private final String GLORP = "glorp";

        private Book book;

        @BeforeEach
        void addUsersToQueue(){
            book = library.getBook(BOOK_TITLE);
            library.addToHoldQueue(BOOK_TITLE, RYAN);
            library.addToHoldQueue(BOOK_TITLE, GLORP);
        }

        @Test
        @DisplayName("Add 2 borrowers to queue and check size is 2")
        void RESP_17_test_1(){
            assertEquals(2, book.getHoldersNum());
        }

        @Test
        @DisplayName("Add 'ryan' first and then 'glorp', check user 'ryan' is at the head")
        void RESP_17_test_2(){
            assertEquals(RYAN, book.peekHolderQueue().getUsername());
        }

        @Test
        @DisplayName("Add 2 borrowers and adding a duplicate borrower throws IllegalStateException")
        void RESP_17_test_3(){
            assertThrows(IllegalStateException.class, () -> library.addToHoldQueue(BOOK_TITLE, RYAN));
        }
    }

    @Nested
    @DisplayName("RESP-25: remove a borrower from a book's queue")
    public class RemoveBorrowerFromQueue{
        private final String BOOK_TITLE = "Crime and Punishment";

        private Book book;

        @BeforeEach
        void addUsersToBookQueue(){
            book = library.getBook(BOOK_TITLE);
            library.addToHoldQueue(BOOK_TITLE, "ryan");
            library.addToHoldQueue(BOOK_TITLE, "glorp");
        }

        @Test
        @DisplayName("Add 2 borrowers to queue and remove 1, size should be 1")
        void RESP_25_test_1(){
            book.popHolder();
            assertEquals(1, book.getHoldersNum());
        }

        @Test
        @DisplayName("Add 2 borrowers to queue and remove 1, next borrower should be 'glorp'")
        void RESP_25_test_2(){
            book.popHolder();
            assertEquals("glorp", book.peekHolderQueue().getUsername());
        }
    }

    @Nested
    @DisplayName("RESP-21: update a book's current holder")
    public class UpdateBookHolder{
        private final String BOOK_TITLE = "The Handmaid's Tale";
        private final String BORROWER = "ryan";

        private Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("'ryan' is the current holder after placing a hold with no holder nor borrowers in queue")
        void RESP_21_test_1(){
            library.setHolder(BOOK_TITLE, BORROWER);

            assertEquals(BORROWER, book.getCurHolder().getUsername());
        }

        @Test
        @DisplayName("'ryan' is removed from the queue after placing a hold and was first in queue")
        void RESP_21_test_2(){
            library.addToHoldQueue(BOOK_TITLE, BORROWER);
            library.addToHoldQueue(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, BORROWER);

            assertNotEquals(BORROWER, book.peekHolderQueue().getUsername());
        }

        @Test
        @DisplayName("'ryan' is the current holder after placing a hold and was first in queue")
        void RESP_21_test_3(){
            library.addToHoldQueue(BOOK_TITLE, BORROWER);
            library.addToHoldQueue(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, BORROWER);

            assertEquals(BORROWER, book.getCurHolder().getUsername());
        }

        @Test
        @DisplayName("'The Handmaid's Tale' is stored in 'ryan' after placing hold")
        void RESP_21_test_4(){
            library.setHolder(BOOK_TITLE, BORROWER);

            assertEquals(BOOK_TITLE, library.getHeldBook(BORROWER).getTitle());
        }

        @Test
        @DisplayName("'ryan' should be in the queue if the book already has a current holder")
        void RESP_21_test_5(){
            library.setHolder(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, BORROWER);

            User result = book.peekHolderQueue();
            if (result == null) fail("No user in queue");
            assertEquals(BORROWER, result.getUsername());
        }
    }

    @Nested
    @DisplayName("RESP-22: set a book's current borrower")
    public class UpdateBookBorrower{
        private final String BOOK_TITLE = "The Handmaid's Tale";

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Check 'squeex' is the current borrower when there's no holders")
        void RESP_22_test_1(){
            library.setBorrower(BOOK_TITLE, "squeex");

            assertEquals("squeex", book.getCurBorrower().getUsername());
        }

        @Test
        @DisplayName("Check 'glorp' is the current holder after 'squeex' (previous holder) becomes the borrower")
        void RESP_22_test_2(){
            library.addToHoldQueue(BOOK_TITLE, "glorp");
            library.setHolder(BOOK_TITLE, "squeex");
            library.setBorrower(BOOK_TITLE, "squeex");

            assertEquals("glorp", book.getCurHolder().getUsername());
        }

        @Test
        @DisplayName("Check checked-out book was added to borrower")
        void RESP_22_test_3(){
            library.setBorrower(BOOK_TITLE, "squeex");
            BorrowedBooks borrowedBooks = library.getBorrowedBooks("squeex");

            assertEquals(book, borrowedBooks.getBookByTitle(BOOK_TITLE));
        }

        @Test
        @DisplayName("If 'squeex' was the only holder, then there should be no current holder after borrowing")
        void RESP_22_test_4(){
            library.setHolder(BOOK_TITLE, "squeex");
            library.setBorrower(BOOK_TITLE, "squeex");
            BorrowedBooks borrowedBooks = library.getBorrowedBooks("squeex");

            assertNull(book.getCurHolder());
        }

        @Test
        @DisplayName("Check the queue is empty after 'squeex' was the current holder (and became the current borrower) and 'glorp' became the current holder")
        void RESP_22_test_5(){
            library.setHolder(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, "glorp");
            library.setBorrower(BOOK_TITLE, "squeex");
            assertNull(library.getBook(BOOK_TITLE).peekHolderQueue());
        }
    }

    @Nested
    @DisplayName("RESP-26: remove current borrower from a book")
    public class RemoveBorrower{
        private final String BOOK_TITLE = "To Kill a Mockingbird";

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Check book's curBorrower is null")
        void RESP_26_test_1(){
            library.setBorrower(BOOK_TITLE, "ryan");
            library.updateDueDateFromDate(BOOK_TITLE, new LibraryDate(2025, 10, 10));
            library.removeBorrower(BOOK_TITLE, "ryan");

            assertNull(book.getCurBorrower());
        }

        @Test
        @DisplayName("Check book's dueDate is null")
        void RESP_26_test_2(){
            library.setBorrower(BOOK_TITLE, "ryan");
            library.updateDueDateFromDate(BOOK_TITLE, new LibraryDate(2025, 10, 10));
            library.removeBorrower(BOOK_TITLE, "ryan");

            assertNull(book.getDueDate());
        }

        @Test
        @DisplayName("Throw IllegalArgumentException if username doesn't match current borrower")
        void RESP_26_test_3(){
            library.setBorrower(BOOK_TITLE, "ryan");

            assertThrows(IllegalArgumentException.class, () -> library.removeBorrower(BOOK_TITLE, "squeex"));
        }

        @Test
        @DisplayName("Removing a book with no borrower throws NullPointerException")
        void RESP_26_test_4(){
            assertThrows(NullPointerException.class, () -> library.removeBorrower(BOOK_TITLE, "ryan"));
        }

        @Test
        @DisplayName("Check book is absent from borrower's checked-out books")
        void RESP_26_test_5(){
            library.setBorrower(BOOK_TITLE, "ryan");
            library.removeBorrower(BOOK_TITLE, "ryan");

            assertFalse(library.borrowerHasBook(BOOK_TITLE, "ryan"));
        }
    }

    @Nested
    @DisplayName("RESP-11: retrieve appropriate availability status")
    public class RetrieveAvailabilityStatus{
        private final String BOOK_TITLE = "Nineteen Eighty-Four";
        private final String BORROWER = "glorp";

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.AVAILABLE if there's no holders nor borrower")
        void RESP_11_test_1(){
            AvailabilityEnum result = book.getAvailabilityStatus(BORROWER);
            assertEquals(AvailabilityEnum.AVAILABLE, result);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.CHECKED_OUT if there a current borrower")
        void RESP_11_test_2(){
            library.setBorrower(BOOK_TITLE, "ryan");

            AvailabilityEnum result = book.getAvailabilityStatus(BORROWER);
            assertEquals(AvailabilityEnum.CHECKED_OUT, result);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.ON_HOLD if there's a current holder (not current user) but no current borrower")
        void RESP_11_test_3(){
            library.setHolder(BOOK_TITLE, "ryan");

            AvailabilityEnum result = book.getAvailabilityStatus(BORROWER);
            assertEquals(AvailabilityEnum.ON_HOLD, result);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.AVAILABLE if there's no current borrower but current holder is current user")
        void RESP_11_test_4(){
            library.setHolder(BOOK_TITLE, "glorp");

            AvailabilityEnum result = book.getAvailabilityStatus(BORROWER);
            assertEquals(AvailabilityEnum.AVAILABLE, result);
        }
    }

    @Nested
    @DisplayName("RESP-15: verify borrower’s eligibility to check out a book ")
    public class VerifyEligibilityToBorrow{
        private final String BOOK_TITLE = "The Science of Beauty";
        private final String CUR_USER = "ryan";

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CHECKED_OUT_BY_ANOTHER if book is checked out by another borrower")
        void RESP_15_test_1(){
            library.setBorrower(BOOK_TITLE, "glorp");
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.CHECKED_OUT_BY_ANOTHER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.ON_HOLD_BY_ANOTHER if the book has no borrower, but is on hold by another borrower")
        void RESP_15_test_2(){
            library.setHolder(BOOK_TITLE, "glorp");
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.ON_HOLD_BY_ANOTHER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CHECKED_OUT_BY_USER if book is checked out by current user")
        void RESP_15_test_3(){
            library.setBorrower(BOOK_TITLE, CUR_USER);
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.CHECKED_OUT_BY_USER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.AT_BORROWING_LIMIT if the book is available, but user is at the 3-book limit")
        void RESP_15_test_4(){
            library.setBorrower("Eragon", CUR_USER);
            library.setBorrower("The Ways of Kings", CUR_USER);
            library.setBorrower(BOOK_TITLE, CUR_USER);
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.AT_BORROWING_LIMIT, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_BORROW if the book has no borrower, no holder, and user is not at the limit")
        void RESP_15_test_5(){
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.CAN_BORROW, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_BORROW if the book has no borrower, the holder is the user, and user is not at the limit")
        void RESP_15_test_6(){
            library.setHolder(BOOK_TITLE, CUR_USER);
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.CAN_BORROW, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.AT_BORROW_LIMIT if the borrower has 3 books and is a current holder of the book")
        void RESP_15_test_7(){
            library.setBorrower("Eragon", CUR_USER);
            library.setBorrower("The Ways of Kings", CUR_USER);
            library.setBorrower("No Longer Human", CUR_USER);
            library.setHolder(BOOK_TITLE, CUR_USER);
            TransactionEnum result = library.verifyBorrowing(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.AT_BORROWING_LIMIT, result);
        }
    }

    @Nested
    @DisplayName("RESP-16: verify borrower's eligibility to place a hold on a book")
    public class VerifyEligibilityToHold{
        private final String BOOK_TITLE = "The Hunger Games";
        private final String CUR_USER = "squeex";

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Returns TransactionEnum.ON_HOLD_BY_USER if the user is the current holder")
        void RESP_16_test_1(){
            library.setHolder(BOOK_TITLE, CUR_USER);
            TransactionEnum result = library.verifyHolding(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.ON_HOLD_BY_USER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.ON_HOLD_BY_USER if the book has another holder, but user is in the hold queue")
        void RESP_16_test_2(){
            library.setHolder(BOOK_TITLE, "glorp");
            library.addToHoldQueue(BOOK_TITLE, CUR_USER);
            TransactionEnum result = library.verifyHolding(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.ON_HOLD_BY_USER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.AT_HOLD_LIMIT if the user is holding another book")
        void RESP_16_test_3(){
            library.setHolder("No Longer Human", CUR_USER);
            TransactionEnum result = library.verifyHolding(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.AT_HOLD_LIMIT, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_HOLD if the book has a hold, the user has no holds, and the user isn't a holder of the book")
        void RESP_16_test_4(){
            library.setHolder(BOOK_TITLE, "glorp");
            TransactionEnum result = library.verifyHolding(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.CAN_HOLD, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_HOLD if the user has no holds and book has no holds")
        void RESP_16_test_5(){
            TransactionEnum result = library.verifyHolding(BOOK_TITLE, CUR_USER);

            assertEquals(TransactionEnum.CAN_HOLD, result);
        }
    }

    @Nested
    @DisplayName("RESP-19: record book borrowing transaction details")
    public class RecordTransaction{

        @BeforeEach
        void initTransactions(){
            library.addBorrowTransaction(new BorrowTransaction("Nineteen Eighty-Four", "glorp", "2025-10-13"));
            library.addBorrowTransaction(new BorrowTransaction("Eragon", "ryan", "2025-11-13"));
            library.addBorrowTransaction(new BorrowTransaction("Moby-Dick", "glorp", "2025-11-20"));
        }

        @Test
        @DisplayName("Add 3 books and check title of 2nd book")
        void RESP_19_test_1(){
            BorrowTransaction borrowTransaction = library.getBorrowTransaction(1);
            assertEquals("Eragon", borrowTransaction.getBookTitle());
        }

        @Test
        @DisplayName("Add 3 books and check borrower of 2nd book")
        void RESP_19_test_2(){
            BorrowTransaction borrowTransaction = library.getBorrowTransaction(1);
            assertEquals("ryan", borrowTransaction.getBorrower());
        }

        @Test
        @DisplayName("Add 3 books and check borrow date of 2nd book")
        void RESP_19_test_3(){
            BorrowTransaction borrowTransaction = library.getBorrowTransaction(1);
            assertEquals("2025-11-13", borrowTransaction.getBorrowDate());
        }

        @Test
        @DisplayName("Check size of borrower transactions is 3")
        void RESP_19_test_4(){
            assertEquals(3, library.getBorrowerTransactionsSize());
        }
    }

    @Nested
    @DisplayName("RESP-30: retrieve all borrowed books, sorted by author")
    public class RetrieveAllBorrowedBooksSorted{
        List<Book> borrowerBooks;

        @BeforeEach
        void initBorrowerBooks(){
            library.setBorrower("The Ways of Kings", "ryan");
            library.setBorrower("The Hunger Games", "ryan");
            library.setBorrower("Red Rising", "ryan");
            borrowerBooks = library.getBorrowedBooksSorted("ryan");
        }

        @ParameterizedTest
        @CsvSource({"0,The Ways of Kings", "1,Red Rising", "2,The Hunger Games"})
        @DisplayName("Add 3 books and check they're at the correct index")
        void RESP_30_test_1(int index, String bookTitle){
            if (borrowerBooks == null)
                fail("books array is null");
            assertEquals(borrowerBooks.get(index).getTitle(), bookTitle);
        }

        @Test
        @DisplayName("Add 3 books and check size is 3")
        void RESP_30_test_2(){
            if (borrowerBooks == null)
                fail("books array is null");
            assertEquals(3, borrowerBooks.size());
        }
    }

    @Nested
    @DisplayName("RESP-33: check if a user's on-hold book is available")
    public class NestedTestClass{
        private final String CUR_USER = "ryan";
        private final String BOOK_TITLE = "Eragon";

        @Test
        @DisplayName("Returns true if user is the current holder of a book and there's no current borrower")
        void RESP_33_test_1(){
            library.setHolder(BOOK_TITLE, CUR_USER);
            assertTrue(library.heldBookIsAvailable(CUR_USER));
        }

        @Test
        @DisplayName("Returns false if the user is the current holder of a book and there's a current borrower")
        void RESP_33_test_2(){
            library.setBorrower(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, CUR_USER);
            assertFalse(library.heldBookIsAvailable(CUR_USER));
        }

        @Test
        @DisplayName("Returns false if the user isn't the current holder, but is in the queue")
        void RESP_33_test_3(){
            library.setBorrower(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, "glorp");
            library.setHolder(BOOK_TITLE, CUR_USER);
            assertFalse(library.heldBookIsAvailable(CUR_USER));
        }

        @Test
        @DisplayName("Returns false if the user has no holds")
        void RESP_33_test_4(){
            assertFalse(library.heldBookIsAvailable(CUR_USER));
        }
    }
}
