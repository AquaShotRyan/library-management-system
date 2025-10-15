package org.library;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InitializationTest {

    @Nested
    @DisplayName("RESP-01: initializing 20 books")
    public class CatalogueInitialization {
        @Test
        @DisplayName("Check library catalogue size is 20")
        void RESP_01_test_1(){
            InitializeLibrary libraryInit = new InitializeLibrary();
            Catalogue catalogue = libraryInit.initCatalogue();

            int size = catalogue.getCatalogueSize();

            assertEquals(20, size);

        }
        @Test
        @DisplayName("Check library catalogue for valid book - Great Gatsby.")
        void RESP_01_test_2(){

            InitializeLibrary libraryInit = new InitializeLibrary();
            Catalogue catalogue = libraryInit.initCatalogue();

            Book book = catalogue.getBook("Great Gatsby");

            String title = book.getTitle();
            assertEquals("Great Gatsby",title);
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
        @DisplayName("Check user count is 3")
        void RESP_02_test_1(){
            int size = borrowers.getBorrowersSize();

            assertEquals(3, size);
        }

        @Test
        @DisplayName("Check borrowers for valid borrower 'ryan'")
        void RESP_02_test_2(){
            Borrower ryan = borrowers.getBorrower("ryan");

            assertEquals("ryan", ryan.getUsername());
        }
    }
}
