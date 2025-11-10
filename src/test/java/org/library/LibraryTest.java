package org.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    Library library;
    private final LibraryDate today = new LibraryDate(Calendar.getInstance());

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
        @DisplayName("Getting all books returns an array of size 20 or bigger")
        void RESP_13_test_1(){
            int resultSize = allBooks.size();

            assertTrue(resultSize >= 20);
        }

        @Test
        @DisplayName("Book with author 'Arthur C. Clarke' is at the first index")
        void RESP_13_test_2(){
            try{
                Book book = allBooks.getFirst();
                String expected = BookData.getArrSortedByAuthor().getFirst().getAuthor();
                assertEquals(expected, book.getAuthor());
            }catch(NoSuchElementException e) {
                fail("Books collection is empty");
            }
        }

        @Test
        @DisplayName("Book with author 'Tom King is at the last index")
        void RESP_13_test_3(){
            try {
                Book book = allBooks.getLast();
                String expected = BookData.getArrSortedByAuthor().getLast().getAuthor();
                assertEquals(expected, book.getAuthor());
            } catch(NoSuchElementException e) {
                fail("Books collection is empty");
            }
        }
    }

    @Nested
    @DisplayName("RESP-20: add book to a borrower's checked-out books")
    public class AddCheckedOutBook {
        private final String CUR_USER = UserData.SQUEEX.getUsername();
        private Book book1;
        private Book book2;
        private Book book3;

        @BeforeEach
        void initBooks(){
            book1 = library.getBook(BookData.BERSERK_DELUXE_VOL_1.getTitle());
            book2 = library.getBook(BookData.MOBY_DICK.getTitle());
            book3 = library.getBook(BookData.BLOOD_MERIDIAN.getTitle());
        }

        @Test
        @DisplayName("Check added book 'Berserk Deluxe Volume 1' is in Borrower's borrowed books")
        void RESP_20_test_1(){
            library.addBookToBorrower(book1, CUR_USER);

            BorrowedBooks borrowedBooks = library.getBorrowedBooks(CUR_USER);
            Book borrowedBook = borrowedBooks.getBookByTitle(book1.getTitle());

            assertEquals(book1.getTitle(), borrowedBook.getTitle());
        }

        @Test
        @DisplayName("Add 3 books and check the 2nd book, 'Moby-Dick,' is in Borrower's borrowed books")
        void RESP_20_test_2(){
            library.addBookToBorrower(book1, CUR_USER);
            library.addBookToBorrower(book2, CUR_USER);
            library.addBookToBorrower(book3, CUR_USER);

            BorrowedBooks borrowedBooks = library.getBorrowedBooks(CUR_USER);
            Book borrowedBook = borrowedBooks.getBookByTitle(book2.getTitle());

            assertEquals(book2.getTitle(), borrowedBook.getTitle());
        }

        @Test
        @DisplayName("Add 3 books and check size is 3")
        void RESP_20_test_3(){
            library.addBookToBorrower(book1, CUR_USER);
            library.addBookToBorrower(book2, CUR_USER);
            library.addBookToBorrower(book3, CUR_USER);

            BorrowedBooks borrowedBooks = library.getBorrowedBooks(CUR_USER);

            assertEquals(3, borrowedBooks.size());
        }

        @Test
        @DisplayName("Adding a duplicate book throws IllegalStateException")
        void RESP_20_test_4(){
            library.addBookToBorrower(book1, CUR_USER);

            assertThrows(IllegalStateException.class, () -> library.addBookToBorrower(book1, CUR_USER));
        }
    }

    @Nested
    @DisplayName("RESP-18: update a book's due date to 14 days from today")
    public class BookDueDate{
        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BookData.RED_RISING.getTitle());
;        }

        @Test
        @DisplayName("If today is 2025-09-05, then due date is 2025-09-19")
        void RESP_18_test_1(){
            LibraryDate today = new LibraryDate(2025, Calendar.SEPTEMBER, 5);
            LibraryDate expectedDate = new LibraryDate(2025, Calendar.SEPTEMBER, 19);

            library.setDueDateFromDate(book.getTitle(), today);

            LibraryDate dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }

        @Test
        @DisplayName("If today is 2025-10-24, then due date is 2025-11-07")
        void RESP_18_test_2(){
            LibraryDate today = new LibraryDate(2025, Calendar.OCTOBER, 24);
            LibraryDate expectedDate = new LibraryDate(2025, Calendar.NOVEMBER, 7);

            library.setDueDateFromDate(book.getTitle(), today);

            LibraryDate dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }

        @Test
        @DisplayName("If today is 2025-12-29, then due date is 2026-01-12")
        void RESP_18_test_3(){
            LibraryDate  today = new LibraryDate (2025, Calendar.DECEMBER, 29);
            LibraryDate  expectedDate = new LibraryDate(2026, Calendar.JANUARY, 12);

            library.setDueDateFromDate(book.getTitle(), today);

            LibraryDate  dueDate = book.getDueDate();

            assertEquals(expectedDate, dueDate);
        }
    }

    @Nested
    @DisplayName("RESP-24: remove a book from a borrower's checked-out books")
    public class RemoveCheckedOutBook {
        private final String CUR_USER = UserData.RYAN.getUsername();
        private final String BOOK1_TITLE = BookData.SUPERGIRL_WOMAN_OF_TOMORROW_1.getTitle();
        private final String BOOK2_TITLE = BookData.THE_ART_AND_MAKING_OF_ARCANE.getTitle();

        @BeforeEach
        void addBorrowedBooks() {
            library.setBorrower(BOOK1_TITLE, CUR_USER);
            library.setBorrower(BOOK2_TITLE, CUR_USER);
        }

        @Test
        @DisplayName("Remove 1 book from collection of 2, size should be 1")
        void RESP_24_test_1(){
            library.removeBookFromBorrower(BOOK1_TITLE, CUR_USER);
            int result = library.getBorrowedBooksNum(CUR_USER);

            assertEquals(1, result);
        }

        @Test
        @DisplayName("Remove book 'Supergirl: Woman of Tomorrow #1', finding it should return null")
        void RESP_24_test_2(){
            library.removeBookFromBorrower(BOOK1_TITLE, CUR_USER);
            Book result = library.getBorrowedBooks(CUR_USER).getBookByTitle(BOOK1_TITLE);

            assertNull(result);
        }

        @Test
        @DisplayName("Remove book 'Supergirl: Woman of Tomorrow #1', removing it again should throw UnsupportedOperationException")
        void RESP_24_test_3(){
            library.removeBookFromBorrower(BOOK1_TITLE, CUR_USER);
            assertThrows(UnsupportedOperationException.class, () -> library.removeBookFromBorrower(BOOK1_TITLE, CUR_USER));
        }
    }

    @Nested
    @DisplayName("RESP-17: add borrower to book's queue")
    public class AddBorrowerToQueue{
        private final String BOOK_TITLE = BookData.CRIME_AND_PUNISHMENT.getTitle();
        private final String RYAN = UserData.RYAN.getUsername();
        private final String GLORP = UserData.GLORP.getUsername();

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
            assertEquals(RYAN, book.getCurHolder().getUsername());
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
        private final String BOOK_TITLE = BookData.CRIME_AND_PUNISHMENT.getTitle();

        private Book book;

        @BeforeEach
        void addUsersToBookQueue(){
            book = library.getBook(BOOK_TITLE);
            library.addToHoldQueue(BOOK_TITLE, UserData.RYAN.getUsername());
            library.addToHoldQueue(BOOK_TITLE, UserData.GLORP.getUsername());
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
            assertEquals(UserData.GLORP.getUsername(), book.getCurHolder().getUsername());
        }
    }

    @Nested
    @DisplayName("RESP-21: update a book's current holder")
    public class UpdateBookHolder{
        private final String BOOK_TITLE = BookData.THE_HANDMAIDS_TALE.getTitle();
        private final String USER1_NAME = UserData.RYAN.getUsername();
        private final String USER2_NAME = UserData.SQUEEX.getUsername();

        private Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("'ryan' is the current holder after placing a hold with no holder nor borrowers in queue")
        void RESP_21_test_1(){
            library.placeHold(BOOK_TITLE, USER1_NAME);

            assertEquals(USER1_NAME, book.getCurHolder().getUsername());
        }

        @Disabled("Refactored to remove separation of current holder and queue")
        @Test
        @DisplayName("'ryan' is removed from the queue after placing a hold and was first in queue")
        void RESP_21_test_2(){
            library.addToHoldQueue(BOOK_TITLE, USER1_NAME);
            library.addToHoldQueue(BOOK_TITLE, USER2_NAME);
            library.placeHold(BOOK_TITLE, USER1_NAME);

            assertNotEquals(USER1_NAME, book.getCurHolder().getUsername());
        }

        @Test
        @DisplayName("'ryan' is the current holder after placing a hold and was first in queue")
        void RESP_21_test_3(){
            library.addToHoldQueue(BOOK_TITLE, USER1_NAME);
            library.addToHoldQueue(BOOK_TITLE, USER2_NAME);
            library.placeHold(BOOK_TITLE, USER1_NAME);

            assertEquals(USER1_NAME, book.getCurHolder().getUsername());
        }

        @Test
        @DisplayName("'The Handmaid's Tale' is stored in 'ryan' after placing hold")
        void RESP_21_test_4(){
            library.placeHold(BOOK_TITLE, USER1_NAME);

            assertEquals(BOOK_TITLE, library.getHeldBook(USER1_NAME).getTitle());
        }

        @Disabled("Refactored to remove separation of current holder and queue")
        @Test
        @DisplayName("'ryan' should be in the queue if the book already has a current holder")
        void RESP_21_test_5(){
            library.placeHold(BOOK_TITLE, USER2_NAME);
            library.placeHold(BOOK_TITLE, USER1_NAME);

            User result = book.getCurHolder();
            if (result == null) fail("No user in queue");
            assertEquals(USER1_NAME, result.getUsername());
        }
    }

    @Nested
    @DisplayName("RESP-22: set a book's current borrower")
    public class UpdateBookBorrower{
        private final String BOOK_TITLE = BookData.THE_HANDMAIDS_TALE.getTitle();
        private final String USER1_NAME = UserData.SQUEEX.getUsername();
        private final String USER2_NAME = UserData.GLORP.getUsername();

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Check 'squeex' is the current borrower when there's no holders")
        void RESP_22_test_1(){
            library.setBorrower(BOOK_TITLE, USER1_NAME);

            assertEquals(USER1_NAME, book.getCurBorrower().getUsername());
        }

        @Test
        @DisplayName("Check 'glorp' is the current holder after 'squeex' (previous holder) becomes the borrower")
        void RESP_22_test_2(){
            library.addToHoldQueue(BOOK_TITLE, USER2_NAME);
            library.placeHold(BOOK_TITLE, USER1_NAME);
            library.setBorrower(BOOK_TITLE, USER1_NAME);

            assertEquals(USER2_NAME, book.getCurHolder().getUsername());
        }

        @Test
        @DisplayName("Check checked-out book was added to borrower")
        void RESP_22_test_3(){
            library.setBorrower(BOOK_TITLE, USER1_NAME);
            BorrowedBooks borrowedBooks = library.getBorrowedBooks(USER1_NAME);

            assertEquals(book, borrowedBooks.getBookByTitle(BOOK_TITLE));
        }

        @Test
        @DisplayName("If 'squeex' was the only holder, then there should be no current holder after borrowing")
        void RESP_22_test_4(){
            library.placeHold(BOOK_TITLE, USER1_NAME);
            library.setBorrower(BOOK_TITLE, USER1_NAME);
            BorrowedBooks borrowedBooks = library.getBorrowedBooks(USER1_NAME);

            assertNull(book.getCurHolder());
        }

        @Disabled("Refactored to remove separation of current holder and queue")
        @Test
        @DisplayName("Check the queue is empty after 'squeex' was the current holder (and became the current borrower) and 'glorp' became the current holder")
        void RESP_22_test_5(){
            library.placeHold(BOOK_TITLE, USER1_NAME);
            library.placeHold(BOOK_TITLE, USER2_NAME);
            library.setBorrower(BOOK_TITLE, USER1_NAME);
            assertNull(library.getBook(BOOK_TITLE).getCurHolder());
        }
    }

    @Nested
    @DisplayName("RESP-26: remove current borrower from a book")
    public class RemoveBorrower{
        private final String BOOK_TITLE = BookData.TO_KILL_A_MOCKINGBIRD.getTitle();
        private final String USER_NAME = UserData.RYAN.getUsername();

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Check book's curBorrower is null")
        void RESP_26_test_1(){
            library.setBorrower(BOOK_TITLE, USER_NAME);
            library.setDueDateFromDate(BOOK_TITLE, new LibraryDate(2025, 10, 10));
            library.removeBorrowerFromBook(BOOK_TITLE, USER_NAME);

            assertNull(book.getCurBorrower());
        }

        @Test
        @DisplayName("Check book's dueDate is null")
        void RESP_26_test_2(){
            library.setBorrower(BOOK_TITLE, USER_NAME);
            library.setDueDateFromDate(BOOK_TITLE, new LibraryDate(2025, 10, 10));
            library.removeBorrowerFromBook(BOOK_TITLE, USER_NAME);

            assertNull(book.getDueDate());
        }

        @Test
        @DisplayName("Throw IllegalArgumentException if username doesn't match current borrower")
        void RESP_26_test_3(){
            library.setBorrower(BOOK_TITLE, USER_NAME);

            assertThrows(IllegalArgumentException.class, () -> library.removeBorrowerFromBook(BOOK_TITLE, "squeex"));
        }

        @Test
        @DisplayName("Removing a book with no borrower throws NullPointerException")
        void RESP_26_test_4(){
            assertThrows(NullPointerException.class, () -> library.removeBorrowerFromBook(BOOK_TITLE, "ryan"));
        }

        @Test
        @DisplayName("Check book is absent from borrower's checked-out books")
        void RESP_26_test_5(){
            library.setBorrower(BOOK_TITLE, USER_NAME);
            library.removeBorrowerFromBook(BOOK_TITLE, USER_NAME);

            assertFalse(library.borrowerHasBook(BOOK_TITLE, USER_NAME));
        }
    }

    @Nested
    @DisplayName("RESP-11: retrieve appropriate availability status")
    public class RetrieveAvailabilityStatus{
        private final String BOOK_TITLE = BookData.NINETEEN_EIGHTY_FOUR.getTitle();
        private final String USER1_NAME = UserData.GLORP.getUsername();
        private final String USER2_NAME = UserData.RYAN.getUsername();

        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK_TITLE);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.AVAILABLE if there's no holders nor borrower")
        void RESP_11_test_1(){
            AvailabilityEnum result = book.getAvailabilityStatus(USER1_NAME);
            assertEquals(AvailabilityEnum.AVAILABLE, result);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.CHECKED_OUT if there a current borrower")
        void RESP_11_test_2(){
            library.setBorrower(BOOK_TITLE, USER2_NAME);

            AvailabilityEnum result = book.getAvailabilityStatus(USER1_NAME);
            assertEquals(AvailabilityEnum.CHECKED_OUT, result);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.ON_HOLD if there's a current holder (not current user) but no current borrower")
        void RESP_11_test_3(){
            library.placeHold(BOOK_TITLE, USER2_NAME);

            AvailabilityEnum result = book.getAvailabilityStatus(USER1_NAME);
            assertEquals(AvailabilityEnum.ON_HOLD, result);
        }

        @Test
        @DisplayName("Returns AvailabilityEnum.AVAILABLE if there's no current borrower but current holder is current user")
        void RESP_11_test_4(){
            library.placeHold(BOOK_TITLE, USER1_NAME);

            AvailabilityEnum result = book.getAvailabilityStatus(USER1_NAME);
            assertEquals(AvailabilityEnum.AVAILABLE, result);
        }
    }

    @Nested
    @DisplayName("RESP-15: verify borrower’s eligibility to check out a book ")
    public class VerifyEligibilityToBorrow{
        private final String BOOK1_TITLE = BookData.THE_SCIENCE_OF_BEAUTY.getTitle();
        private final String BOOK2_TITLE = BookData.ERAGON.getTitle();
        private final String BOOK3_TITLE = BookData.THE_WAY_OF_KINGS.getTitle();
        private final String BOOK4_TITLE = BookData.NO_LONGER_HUMAN.getTitle();
        private final String USER1_NAME = UserData.RYAN.getUsername();
        private final String USER2_NAME = UserData.GLORP.getUsername();


        Book book;

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK1_TITLE);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CHECKED_OUT_BY_ANOTHER if book is checked out by another borrower")
        void RESP_15_test_1(){
            library.setBorrower(BOOK1_TITLE, USER2_NAME);
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.CHECKED_OUT_BY_ANOTHER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.ON_HOLD_BY_ANOTHER if the book has no borrower, but is on hold by another borrower")
        void RESP_15_test_2(){
            library.placeHold(BOOK1_TITLE, USER2_NAME);
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.ON_HOLD_BY_ANOTHER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CHECKED_OUT_BY_USER if book is checked out by current user")
        void RESP_15_test_3(){
            library.setBorrower(BOOK1_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.CHECKED_OUT_BY_USER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.AT_BORROWING_LIMIT if the book is available, but user is at the 3-book limit")
        void RESP_15_test_4(){
            library.setBorrower(BOOK2_TITLE, USER1_NAME);
            library.setBorrower(BOOK3_TITLE, USER1_NAME);
            library.setBorrower(BOOK1_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.AT_BORROWING_LIMIT, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_BORROW if the book has no borrower, no holder, and user is not at the limit")
        void RESP_15_test_5(){
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.CAN_BORROW, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_BORROW if the book has no borrower, the holder is the user, and user is not at the limit")
        void RESP_15_test_6(){
            library.placeHold(BOOK1_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.CAN_BORROW, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.AT_BORROW_LIMIT if the borrower has 3 books and is a current holder of the book")
        void RESP_15_test_7(){
            library.setBorrower(BOOK2_TITLE, USER1_NAME);
            library.setBorrower(BOOK3_TITLE, USER1_NAME);
            library.setBorrower(BOOK4_TITLE, USER1_NAME);
            library.placeHold(BOOK1_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyBorrowing(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.AT_BORROWING_LIMIT, result);
        }
    }

    @Nested
    @DisplayName("RESP-16: verify borrower's eligibility to place a hold on a book")
    public class VerifyEligibilityToHold{
        private final String BOOK1_TITLE = BookData.THE_HUNGER_GAMES.getTitle();
        private final String BOOK2_TITLE = BookData.NO_LONGER_HUMAN.getTitle();
        private final String USER1_NAME = UserData.SQUEEX.getUsername();
        private final String USER2_NAME = UserData.GLORP.getUsername();

        Book book;

        void setAtBookLimit(String username){
            LibraryDate today = new LibraryDate(Calendar.getInstance());
            library.checkoutBook(BookData.WAR_AND_PEACE.getTitle(), username, today);
            library.checkoutBook(BookData.BERSERK_DELUXE_VOL_1.getTitle(), username, today);
            library.checkoutBook(BookData.THE_APOTHECARY_DIARIES_VOL_1.getTitle(), username, today);
        }

        @BeforeEach
        void initBook(){
            book = library.getBook(BOOK1_TITLE);
        }

        @Test
        @DisplayName("Returns TransactionEnum.ON_HOLD_BY_USER if the user is the current holder")
        void RESP_16_test_1(){
            library.placeHold(BOOK1_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyHolding(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.ON_HOLD_BY_USER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.ON_HOLD_BY_USER if the book has another holder, but user is in the hold queue")
        void RESP_16_test_2(){
            library.placeHold(BOOK1_TITLE, USER2_NAME);
            library.addToHoldQueue(BOOK1_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyHolding(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.ON_HOLD_BY_USER, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.AT_HOLD_LIMIT if the user is holding another book")
        void RESP_16_test_3(){
            library.placeHold(BOOK2_TITLE, USER1_NAME);
            TransactionEnum result = library.verifyHolding(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.AT_HOLD_LIMIT, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_HOLD if the book has a hold and the user has no holds")
        void RESP_16_test_4(){
            library.placeHold(BOOK1_TITLE, USER2_NAME);
            TransactionEnum result = library.verifyHolding(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.CAN_HOLD, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.IS_AVAILABLE if the user is at the 3-book limit and the book is available")
        void RESP_16_test_5(){
            setAtBookLimit(USER1_NAME);

            TransactionEnum result = library.verifyHolding(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.BOOK_IS_AVAILABLE, result);
        }

        @Test
        @DisplayName("Returns TransactionEnum.CAN_HOLD if the user is at the 3-book limit, the book is being borrowed, and the user has no holds")
        void RESP_16_test_6(){
            setAtBookLimit(USER1_NAME);
            library.setBorrower(BOOK1_TITLE, USER1_NAME);

            TransactionEnum result = library.verifyHolding(BOOK1_TITLE, USER1_NAME);

            assertEquals(TransactionEnum.CAN_HOLD, result);
        }
    }

    @Nested
    @DisplayName("RESP-19: record book borrowing transaction details")
    public class RecordTransaction{
        private final String BOOK1_TITLE = BookData.NINETEEN_EIGHTY_FOUR.getTitle();
        private final String BOOK2_TITLE = BookData.ERAGON.getTitle();
        private final String BOOK3_TITLE = BookData.MOBY_DICK.getTitle();
        private final String USER1_NAME = UserData.GLORP.getUsername();
        private final String USER2_NAME = UserData.RYAN.getUsername();

        @BeforeEach
        void initTransactions(){
            library.addBorrowTransaction(new BorrowTransaction(BOOK1_TITLE, USER1_NAME, "2025-10-13"));
            library.addBorrowTransaction(new BorrowTransaction(BOOK2_TITLE, USER2_NAME, "2025-11-13"));
            library.addBorrowTransaction(new BorrowTransaction(BOOK3_TITLE, USER1_NAME, "2025-11-20"));
        }

        @Test
        @DisplayName("Add 3 books and check title of 2nd book")
        void RESP_19_test_1(){
            BorrowTransaction borrowTransaction = library.getBorrowTransaction(1);
            assertEquals(BOOK2_TITLE, borrowTransaction.getBookTitle());
        }

        @Test
        @DisplayName("Add 3 books and check borrower of 2nd book")
        void RESP_19_test_2(){
            BorrowTransaction borrowTransaction = library.getBorrowTransaction(1);
            assertEquals(USER2_NAME, borrowTransaction.getBorrower());
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
        private final String USER1_NAME = UserData.RYAN.getUsername();
        List<Book> borrowerBooks;

        @BeforeEach
        void initBorrowerBooks(){
            library.setBorrower(BookData.THE_WAY_OF_KINGS.getTitle(), USER1_NAME);
            library.setBorrower(BookData.THE_HUNGER_GAMES.getTitle(), USER1_NAME);
            library.setBorrower(BookData.RED_RISING.getTitle(), USER1_NAME);
            borrowerBooks = library.getBorrowedBooksSorted(USER1_NAME);
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
    public class NotifyOnHoldBookIsAvailable{
        private final String USER1_NAME = UserData.RYAN.getUsername();
        private final String BOOK_TITLE = BookData.ERAGON.getTitle();

        @Test
        @DisplayName("Returns true if user is the current holder of a book and there's no current borrower")
        void RESP_33_test_1(){
            library.placeHold(BOOK_TITLE, USER1_NAME);
            assertTrue(library.heldBookIsAvailable(USER1_NAME));
        }

        @Test
        @DisplayName("Returns false if the user is the current holder of a book and there's a current borrower")
        void RESP_33_test_2(){
            library.setBorrower(BOOK_TITLE, UserData.SQUEEX.getUsername());
            library.placeHold(BOOK_TITLE, USER1_NAME);
            assertFalse(library.heldBookIsAvailable(USER1_NAME));
        }

        @Test
        @DisplayName("Returns false if the user isn't the current holder, but is in the queue")
        void RESP_33_test_3(){
            library.setBorrower(BOOK_TITLE, UserData.SQUEEX.getUsername());
            library.placeHold(BOOK_TITLE, UserData.GLORP.getUsername());
            library.placeHold(BOOK_TITLE, USER1_NAME);
            assertFalse(library.heldBookIsAvailable(USER1_NAME));
        }

        @Test
        @DisplayName("Returns false if the user has no holds")
        void RESP_33_test_4(){
            assertFalse(library.heldBookIsAvailable(USER1_NAME));
        }
    }

    @Nested
    @DisplayName("Additional Tests (i.e. not for assignment 1)")
    public class OtherTests{
        private final String USER1_NAME = UserData.SQUEEX.getUsername();
        private final String USER2_NAME = UserData.GLORP.getUsername();
        private final String BOOK_TITLE = BookData.THE_ODYSSEY.getTitle();

        @Test
        @DisplayName("When all books are available, size should match number of books in BookData")
        void getAllBooksWithAvailability_test_1(){
            int result = library.getAllBooksWithAvailability(USER1_NAME, AvailabilityEnum.AVAILABLE).size();
            assertEquals(BookData.values().length, result);
        }

        @Test
        @DisplayName("When one book is checked out, size should be -1 from number of books in BookData")
        void getAllBooksWithAvailability_test_2(){
            library.checkoutBook(BOOK_TITLE, USER1_NAME, today);
            int result = library.getAllBooksWithAvailability(USER1_NAME, AvailabilityEnum.AVAILABLE).size();
            assertEquals(BookData.values().length-1, result);
        }

        @Test
        @DisplayName("When one book is on hold, size should be -1 from number of books in BookData")
        void getAllBooksWithAvailability_test_3(){
            library.checkoutBook(BOOK_TITLE, USER1_NAME, today);
            library.placeHold(BOOK_TITLE, USER2_NAME);
            library.removeBorrowerFromBook(BOOK_TITLE, USER1_NAME);

            int result = library.getAllBooksWithAvailability(USER1_NAME, AvailabilityEnum.AVAILABLE).size();
            assertEquals(BookData.values().length-1, result);
        }
    }
}
