package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
}
