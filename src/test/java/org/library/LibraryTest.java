package org.library;

import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {

    @Nested
    @DisplayName("RESP-01: initializing 20 books")
    public class CatalogueInitialization {
        @Test
        @DisplayName("Check library catalogue size is 20")
        void RESP_01_test_1(){
            InitializeLibrary library = new InitializeLibrary();
            Catalogue catalogue = library.initCatalogue();

            int size = catalogue.getCatalogueSize();

            assertEquals(20, size);

        }
        @Test
        @DisplayName("Check library catalogue for valid book - Great Gatsby.")
        void RESP_01_test_2(){

            InitializeLibrary library = new InitializeLibrary();
            Catalogue catalogue = library.initCatalogue();

            Book book = catalogue.getBook("Great Gatsby");

            String title = book.getTitle();
            assertEquals("Great Gatsby",title);
        }
    }

    @Nested
    @DisplayName("RESP-13: retrieve all books sorted by author")
    public class GetAllBooks {
        private Library library;

        @BeforeEach
        void initLibrary(){
            library = new Library();
        }

        @Test
        @DisplayName("Getting all books returns an array of size 20")
        void RESP_13_test_1(){
            List<Book> allBooks = library.getAllBooks();
            int resultSize = allBooks.size();

            assertEquals(20, resultSize);
        }

        @Test
        @DisplayName("Book with author 'Arthur C. Clarke' is at the first index")
        void RESP_13_test_2(){
            List<Book> allBooks = library.getAllBooks();
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
            List<Book> allBooks = library.getAllBooks();
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
        Library library;

        @BeforeEach
        void initLibrary() {
            library = new Library();
        }

        @Test
        @DisplayName("Check added book 'Berserk Deluxe Volume 1' is in Borrower's borrowed books")
        void RESP_20_test_1(){
            Book book = library.getBook("Berserk Deluxe Volume 1");
            library.addBookToBorrower(book, "squeex");

            BorrowedBooks borrowedBooks = library.getBorrowedBooks("squeex");
            Book borrowedBook = borrowedBooks.getBookByTitle(book.getTitle());

            assertEquals(book.getTitle(), borrowedBook.getTitle());
        }

        @Test
        @DisplayName("Add 3 books and check the 2nd book, 'Moby-Dick,' is in Borrower's borrowed books")
        void RESP_20_test_2(){
            Book book1 = library.getBook("Blood Meridian");
            Book book2 = library.getBook("Moby-Dick");
            Book book3 = library.getBook("Berserk Deluxe Volume 1");
            library.addBookToBorrower(book1, "squeex");
            library.addBookToBorrower(book2, "squeex");
            library.addBookToBorrower(book3, "squeex");

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
        private Library library;

        @BeforeEach
        void initLibrary(){
            library = new Library();
        }

        @Test
        @DisplayName("If today is 2025-09-05, then due date is 2025-09-19")
        void RESP_18_test_1(){

            Calendar today = new GregorianCalendar(2025, Calendar.SEPTEMBER, 5);
            Calendar expectedDate = new GregorianCalendar(2025, Calendar.SEPTEMBER, 19);

            Book book = library.getBook("Red Rising");
            library.updateDueDateFromDate(book.getTitle(), today);

            Calendar dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }

        @Test
        @DisplayName("If today is 2025-10-24, then due date is 2025-11-07")
        void RESP_18_test_2(){
            Calendar today = new GregorianCalendar(2025, Calendar.OCTOBER, 24);
            Calendar expectedDate = new GregorianCalendar(2025, Calendar.NOVEMBER, 7);

            Book book = library.getBook("Red Rising");
            library.updateDueDateFromDate(book.getTitle(), today);

            Calendar dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }

        @Test
        @DisplayName("If today is 2025-12-29, then due date is 2026-01-12")
        void RESP_18_test_3(){
            Calendar today = new GregorianCalendar(2025, Calendar.DECEMBER, 29);
            Calendar expectedDate = new GregorianCalendar(2026, Calendar.JANUARY, 12);

            Book book = library.getBook("Red Rising");
            library.updateDueDateFromDate(book.getTitle(), today);

            Calendar dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }


    }

    @Nested
    @DisplayName("RESP-24: remove a book from a borrower's checked-out books")
    public class RemoveCheckedOutBook {
        Library library;
        private final String TEST_USERNAME = "ryan";
        private final String SUPER_GIRL_BOOK = "Supergirl: Woman of Tomorrow #1";
        private final String ARCANE_BOOK = "The Art and Making of Arcane";

        @BeforeEach
        void addBorrowedBooks() {
            library = new Library();
            Book book1 = library.getBook(SUPER_GIRL_BOOK);
            Book book2 = library.getBook(ARCANE_BOOK);
            library.addBookToBorrower(book1, TEST_USERNAME);
            library.addBookToBorrower(book2, TEST_USERNAME);
        }

        @Test
        @DisplayName("Remove 1 book from collection of 2, size should be 1")
        void RESP_24_test_1(){
            library.removeBookFromBorrower(SUPER_GIRL_BOOK, TEST_USERNAME);
            int result = library.getBorrowedBooksNum(TEST_USERNAME);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("Remove book 'Supergirl: Woman of Tomorrow #1', finding it should return null")
        void RESP_24_test_2(){
            library.removeBookFromBorrower(SUPER_GIRL_BOOK, TEST_USERNAME);
            Book result = library.getBorrowedBooks(TEST_USERNAME).getBookByTitle(SUPER_GIRL_BOOK);

            assertNull(result);
        }

        @Test
        @DisplayName("Remove book 'Supergirl: Woman of Tomorrow #1', removing it again should throw UnsupportedOperationException")
        void RESP_24_test_3(){
            library.removeBookFromBorrower(SUPER_GIRL_BOOK, TEST_USERNAME);
            assertThrows(UnsupportedOperationException.class, () -> library.removeBookFromBorrower(SUPER_GIRL_BOOK, TEST_USERNAME));
        }
    }

    @Nested
    @DisplayName("RESP-17: add borrower to book's queue")
    public class AddBorrowerToQueue{
        private final String BOOK_TITLE = "Crime and Punishment";
        private final String RYAN = "ryan";
        private final String GLORP = "glorp";

        private Library library;
        private Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
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

        private Library library;
        private Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
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

        private Library library;
        private Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
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
    }

    @Nested
    @DisplayName("RESP-22: set a book's current borrower")
    public class UpdateBookBorrower{
        private final String BOOK_TITLE = "The Handmaid's Tale";

        Library library;
        Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
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
    }

    @Nested
    @DisplayName("RESP-26: remove current borrower from a book")
    public class RemoveBorrower{
        private final String BOOK_TITLE = "To Kill a Mockingbird";

        Library library;
        Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Check book's curBorrower is null")
        void RESP_26_test_1(){
            library.setBorrower(BOOK_TITLE, "ryan");
            library.updateDueDateFromDate(BOOK_TITLE, new GregorianCalendar(2025, 10, 10));
            library.removeBorrower(BOOK_TITLE, "ryan");

            assertNull(book.getCurBorrower());
        }

        @Test
        @DisplayName("Check book's dueDate is null")
        void RESP_26_test_2(){
            library.setBorrower(BOOK_TITLE, "ryan");
            library.updateDueDateFromDate(BOOK_TITLE, new GregorianCalendar(2025, 10, 10));
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
    }


    @Nested
    @DisplayName("RESP-11: retrieve appropriate availability status")
    public class RetrieveAvailabilityStatus{
        private final String BOOK_TITLE = "Nineteen Eighty-Four";
        private final String BORROWER = "glorp";

        Library library;
        Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
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

        Library library;
        Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
            book = library.getBook(BOOK_TITLE);

            // add 2 books to current user
            Book batman = library.getBook("Absolute Batman #1");
            Book eragon = library.getBook("Eragon");
            library.addBookToBorrower(batman, CUR_USER);
            library.addBookToBorrower(eragon, CUR_USER);
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
            Book apothecary= library.getBook("The Apothecary Diaries: Volume 1");
            library.addBookToBorrower(apothecary, CUR_USER);
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
    }

    @Nested
    @DisplayName("RESP-16: verify borrower's eligibility to place a hold on a book")
    public class VerifyEligibilityToHold{
        private final String BOOK_TITLE = "The Hunger Games";
        private final String CUR_USER = "squeex";

        Library library;
        Book book;

        @BeforeEach
        void initLibrary(){
            library = new Library();
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
}
