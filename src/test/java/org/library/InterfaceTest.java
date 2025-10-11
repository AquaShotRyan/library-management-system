package org.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceTest {

    @Nested
    @DisplayName("RESP-03: prompting username and password")
    public class PromptCredentials{
        private LibraryInterface libraryInterface;
        private StringWriter output;

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @BeforeEach
        void initOutput(){
            output = new StringWriter();
        }

        @Test
        @DisplayName("Check username is prompted")
        void RESP_03_test_1(){
            Scanner input = new Scanner("some_username");

            libraryInterface.promptStringInput(input, new PrintWriter(output), "username: ");

            assertTrue(output.toString().contains("username:"));
        }

        @Test
        @DisplayName("Check password is prompted")
        void RESP_03_test_2(){
            Scanner input = new Scanner("some_password");

            libraryInterface.promptStringInput(input, new PrintWriter(output), "password: ");

            assertTrue(output.toString().contains("password:"));
        }

        @Test
        @DisplayName("Check input 'glorp' was received from the prompt")
        void RESP_03_test_3(){
            String username = "glorp";
            Scanner input = new Scanner(username);

            String result = libraryInterface.promptStringInput(input, new PrintWriter(output), "username: ");

            assertEquals(username, result);
        }
    }

    @Nested
    @DisplayName("RESP-07: prompt menu")
    public class PromptMenu{
        private LibraryInterface libraryInterface;
        private StringWriter output;

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @BeforeEach
        void initOutput(){
            output = new StringWriter();
        }

        @ParameterizedTest
        @EnumSource(names = { "BORROW", "RETURN", "LOGOUT" })
        @DisplayName("Menu displays '1) Borrow a book', '2) Return a book', and '3) Logout'")
        void RESP_07_test_1(MenuEnum option){
            Scanner input = new Scanner("some_input");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertTrue(output.toString().contains(option.getFullOptionDesc()));
        }

        @Test
        @DisplayName("Entering '1', returns MenuEnum.BORROW")
        void RESP_07_test_2(){
            Scanner input = new Scanner("1");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertEquals(MenuEnum.BORROW, selected);
        }

        @Test
        @DisplayName("Entering '2', returns MenuEnum.RETURN")
        void RESP_07_test_3(){
            Scanner input = new Scanner("2");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertEquals(MenuEnum.RETURN, selected);
        }

        @Test
        @DisplayName("Entering '3', returns MenuEnum.LOGOUT")
        void RESP_07_test_4(){
            Scanner input = new Scanner("3");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertEquals(MenuEnum.LOGOUT, selected);
        }

        @Test
        @DisplayName("Entering 'not a number', displays 'ERROR: invalid input'")
        void RESP_07_test_5(){
            Scanner input = new Scanner("not a number");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertTrue(output.toString().contains("ERROR: invalid input"));
        }

        @Test
        @DisplayName("Enter words, returns MenuEnum.INVALID_INPUT")
        void RESP_07_test_6(){
            Scanner input = new Scanner("not a number");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertEquals(MenuEnum.INVALID_INPUT, selected);
        }

        @Test
        @DisplayName("Menu doesn't display '-1)'")
        void RESP_07_test_7(){
            Scanner input = new Scanner("not a number");

            MenuEnum selected = libraryInterface.promptMenu(input, new PrintWriter(output));

            assertFalse(output.toString().contains(MenuEnum.INVALID_INPUT.getFullOptionDesc()));
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "4"})
        @DisplayName("After entering numbers not 1,2, or 3, displays 'ERROR: invalid input'")
        void RESP_07_test_8(String input){
            MenuEnum selected = libraryInterface.promptMenu(new Scanner(input), new PrintWriter(output));

            assertTrue(output.toString().contains("ERROR: invalid input"));
        }
    }

    @Nested
    @DisplayName("RESP-06: display available on-hold book")
    public class OnHoldBookNotification{
        private StringWriter output;
        private LibraryInterface libraryInterface;
        private Library library;

        @BeforeEach
        void initLibrary(){
            libraryInterface = new LibraryInterface();
            library = new Library();
        }

        @Test
        @DisplayName("Displays 'NOTIFICATION: No Longer Human by Osamu Dazai is available!'")
        void RESP_06_test_1(){
            output = new StringWriter();

            Book b = library.getBook("No Longer Human");
            libraryInterface.displayAvailableBookNotification(new PrintWriter(output), b);

            assertTrue(output.toString().contains("NOTIFICATION: No Longer Human by Osamu Dazai is available!"));
        }
    }

    @Nested
    @DisplayName("RESP-09: prompt user confirmation")
    public class PromptUserConfirmation{
        private LibraryInterface libraryInterface;
        private StringWriter output;

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @BeforeEach
        void initOutput(){
            output = new StringWriter();
        }

        @Test
        @DisplayName("Returns true if user types 'y'")
        void RESP_09_test_1(){
            Scanner input = new Scanner("y");
            boolean response = libraryInterface.promptConfirmation(input, new PrintWriter(output), "");

            assertTrue(response);
        }

        @Test
        @DisplayName("Returns false if user types 'any other response")
        void RESP_09_test_2(){
            Scanner input = new Scanner("any other response");
            boolean response = libraryInterface.promptConfirmation(input, new PrintWriter(output), "");

            assertFalse(response);
        }

        @Test
        @DisplayName("Displays 'Confirm this transaction? (y/n): '")
        void RESP_09_test_3(){
            Scanner input = new Scanner("y");
            boolean response = libraryInterface.promptConfirmation(input, new PrintWriter(output), "Confirm this transaction?");

            assertTrue(output.toString().contains("Confirm this transaction? (y/n): "));
        }

        @Test
        @DisplayName("Displays '(y/n): ' if no confirmation message is passed in")
        void RESP_09_test_4(){
            Scanner input = new Scanner("y");
            boolean response = libraryInterface.promptConfirmation(input, new PrintWriter(output), "");

            assertTrue(output.toString().contains("(y/n): "));
        }
    }

    @Nested
    @DisplayName("RESP-12: display current checked-out book count")
    public class DisplayBookCount{
        private LibraryInterface libraryInterface;
        private StringWriter output;

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @BeforeEach
        void initOutput(){
            output = new StringWriter();
        }

        @ParameterizedTest
        @ValueSource(ints = {0,1,2,3})
        @DisplayName("Displays 'Current number of borrowed books: 0'")
        void RESP_12_test_1(int numBooks){
            libraryInterface.displayNumberOfBorrowedBooks(new PrintWriter(output), numBooks);
            String result = output.toString();
            String expected = String.format("Current number of borrowed books: %d", numBooks);

            assertTrue(result.contains(expected), result);
        }
    }
}

