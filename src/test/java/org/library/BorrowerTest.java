package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BorrowerTest {

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
