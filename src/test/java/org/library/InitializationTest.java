package org.library;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class InitializationTest {

    @Nested
    @DisplayName("RESP-01: initializing 20 books")
    public class CatalogueInitialization {
        @Test
        @DisplayName("Check library catalogue size is at least 20")
        void RESP_01_test_1(){
            InitializeLibrary libraryInit = new InitializeLibrary();
            Catalogue catalogue = libraryInit.initCatalogue();

            int size = catalogue.getCatalogueSize();

            assertTrue(size >= 20);

        }
        @Test
        @DisplayName("Check library catalogue for valid book - Great Gatsby.")
        void RESP_01_test_2(){
            final String bookTitle = BookData.GREAT_GATSBY.getTitle();

            InitializeLibrary libraryInit = new InitializeLibrary();
            Catalogue catalogue = libraryInit.initCatalogue();

            Book book = catalogue.getBook(bookTitle);

            String result = book.getTitle();
            assertEquals(bookTitle, result);
        }
    }

    @Nested
    @DisplayName("RESP-02: initializing 3 borrowers")
    public class BorrowerInitialization {
        private Borrowers borrowers;

        @BeforeEach
        void initializeBorrowers(){
            InitializeLibrary library = new InitializeLibrary();
            borrowers = library.initBorrowers();
        }

        @Test
        @DisplayName("Check user count is at least 3")
        void RESP_02_test_1(){
            int size = borrowers.getBorrowersSize();

            assertTrue(size >= 3);
        }

        @Test
        @DisplayName("Check borrowers for valid borrower 'ryan'")
        void RESP_02_test_2(){
            final String username = UserData.RYAN.getUsername();
            Borrower ryan = borrowers.getBorrower(username);

            assertEquals(username, ryan.getUsername());
        }
    }
}
