package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
}
