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
            library.placeHold(BOOK_TITLE, RYAN);
            library.placeHold(BOOK_TITLE, GLORP);
        }

        @Test
        @DisplayName("Add 2 borrowers to queue and check size is 2")
        void RESP_17_test_1(){
            assertEquals(2, book.getHoldersNum());
        }

        @Test
        @DisplayName("Add 'ryan' first and then 'glorp', check user 'ryan' is at the head")
        void RESP_17_test_2(){
            assertEquals(RYAN, book.getFirstHolder().getUsername());
        }

        @Test
        @DisplayName("Add 2 borrowers and adding a duplicate borrower throws IllegalStateException")
        void RESP_17_test_3(){
            assertThrows(IllegalStateException.class, () -> library.placeHold(BOOK_TITLE, RYAN));
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
            library.placeHold(BOOK_TITLE, "ryan");
            library.placeHold(BOOK_TITLE, "glorp");
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
            assertEquals("glorp", book.getFirstHolder().getUsername());
        }
    }

    @Nested
    @DisplayName("RESP-21: update a book's current holder")
    public class NestedTestClass{
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
            library.placeHold(BOOK_TITLE, BORROWER);
            library.placeHold(BOOK_TITLE, "squeex");
            library.setHolder(BOOK_TITLE, BORROWER);

            assertNotEquals(BORROWER, book.getFirstHolder().getUsername());
        }

        @Test
        @DisplayName("'ryan' is the current holder after placing a hold and was first in queue")
        void RESP_21_test_3(){
            library.placeHold(BOOK_TITLE, BORROWER);
            library.placeHold(BOOK_TITLE, "squeex");
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
            library.placeHold(BOOK_TITLE, "glorp");
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
}
