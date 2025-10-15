package org.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceTest {
    private static LibraryInterface libraryInterface;
    private StringWriter output;

    @BeforeAll
    static void initLibraryInterface(){
        libraryInterface = new LibraryInterface();
    }

    @BeforeEach
    void initOutput(){
        output = new StringWriter();
    }

    @Nested
    @DisplayName("RESP-03: prompting username and password")
    public class PromptCredentials{

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
        private Library library;

        @BeforeEach
        void initLibrary(){
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

    @Nested
    @DisplayName("RESP-23: display no borrowed books notification")
    public class DisplayNoBorrowedBooks{

        @Test
        @DisplayName("Displays 'You have no borrowed books to return'")
        void RESP_23_test_1(){
            LibraryInterface libraryInterface = new LibraryInterface();
            StringWriter output = new StringWriter();

            libraryInterface.notifyNoBorrowedBooks(new PrintWriter(output));
            String result = output.toString();

            assertTrue(result.contains("You have no borrowed books to return"), result);
        }
    }

    @Nested
    @DisplayName("RESP-28: display maximum borrowing limit")
    public class DisplayBorrowingLimit{
        @Test
        @DisplayName("Displays 'You have reached the maximum borrowing limit and cannot borrow another book'")
        void RESP_28_test_1(){
            LibraryInterface libraryInterface = new LibraryInterface();
            StringWriter output = new StringWriter();

            libraryInterface.notifyMaxBorrowingLimit(new PrintWriter(output));
            String result = output.toString();

            assertTrue(result.contains("You have reached the maximum borrowing limit and cannot borrow another book"), result);
        }
    }

    @Nested
    @DisplayName("RESP-14: display a book")
    public class DisplayBook{
        private final String BOOK_TITLE = "Great Gatsby";
        private final String TITLE_AUTHOR = "Great Gatsby by F. Scott FitzGerald";
        private final String AVAILABLE = "Available";
        private final String CHECKED_OUT = "Checked Out";
        private final String ON_HOLD = "On Hold";
        private final String NO_DUE_DATE = "due: N/A";
        private final LibraryDate START_DATE = new LibraryDate(2025, GregorianCalendar.JUNE, 20);
        private final String DUE_DATE = "due: 2025-07-04";
        private final String CUR_USER = "squeex";

        private LibraryInterface libraryInterface;
        private StringWriter output;
        Library library;
        Book book;

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @BeforeEach
        void initOutput(){
            output = new StringWriter();
        }

        @BeforeEach
        void initLibrary(){
            library = new Library();
            book = library.getBook("Great Gatsby");
        }

        @ParameterizedTest
        @ValueSource(strings = {TITLE_AUTHOR, AVAILABLE, NO_DUE_DATE})
        @DisplayName("Display book with no borrowers nor holders")
        void RESP_14_test_1(String expected){
            libraryInterface.displayBook(new PrintWriter(output), book, CUR_USER);

            String result = output.toString();
            assertTrue(result.contains(expected), result);
        }

        @ParameterizedTest
        @ValueSource(strings = {TITLE_AUTHOR, AVAILABLE, NO_DUE_DATE})
        @DisplayName("Display book with no borrower, but on hold by user")
        void RESP_14_test_2(String expected){
            library.setHolder(BOOK_TITLE, CUR_USER);
            libraryInterface.displayBook(new PrintWriter(output), book, CUR_USER);

            String result = output.toString();
            assertTrue(result.contains(expected), result);
        }

        @ParameterizedTest
        @ValueSource(strings = {TITLE_AUTHOR, ON_HOLD, NO_DUE_DATE})
        @DisplayName("Display book with no borrower, but on hold by another borrower")
        void RESP_14_test_3(String expected){
            library.setHolder(BOOK_TITLE, "ryan");
            libraryInterface.displayBook(new PrintWriter(output), book, CUR_USER);

            String result = output.toString();
            assertTrue(result.contains(expected), result);
        }

        @ParameterizedTest
        @ValueSource(strings = {TITLE_AUTHOR, CHECKED_OUT, DUE_DATE})
        @DisplayName("Display book where the user is the current borrower")
        void RESP_14_test_4(String expected)    {
            library.setBorrower(BOOK_TITLE, CUR_USER);
            library.updateDueDateFromDate(BOOK_TITLE, START_DATE);
            libraryInterface.displayBook(new PrintWriter(output), book, CUR_USER);

            String result = output.toString();
            assertTrue(result.contains(expected), result);
        }

        @ParameterizedTest
        @ValueSource(strings = {TITLE_AUTHOR, CHECKED_OUT, NO_DUE_DATE})
        @DisplayName("Display book with a current borrower (not user)")
        void RESP_14_test_5(String expected){
            library.setBorrower(BOOK_TITLE, "ryan");
            library.updateDueDateFromDate(BOOK_TITLE, START_DATE);
            libraryInterface.displayBook(new PrintWriter(output), book, CUR_USER);

            String result = output.toString();
            assertTrue(result.contains(expected), result);
        }
    }

    @Nested
    @DisplayName("RESP-08: prompt user to select a book")
    public class PromptBookSelection{

        @Test
        @DisplayName("Entering a non-negative number returns the same number")
        void RESP_08_test_1(){
            Scanner input = new Scanner("5");
            int result = libraryInterface.promptBookSelection(input, new PrintWriter(output));

            assertEquals(5, result);
        }

        @Test
        @DisplayName("Entering words displays 'ERROR: invalid input'")
        void RESP_08_test_2(){
            Scanner input = new Scanner("not a number");
            int result = libraryInterface.promptBookSelection(input, new PrintWriter(output));

            assertTrue(output.toString().contains("ERROR: invalid input"));
        }

        @Test
        @DisplayName("Prompt displays 'Enter a book number: '")
        void RESP_08_test_3(){
            Scanner input = new Scanner("4");
            int result = libraryInterface.promptBookSelection(input, new PrintWriter(output));

            assertTrue(output.toString().contains("Enter a book number: "));
        }

        @Test
        @DisplayName("Entering words returns -1")
        void RESP_08_test_4(){
            Scanner input = new Scanner("not a number");
            int result = libraryInterface.promptBookSelection(input, new PrintWriter(output));
            assertEquals(-1, result);
        }

        @Test
        @DisplayName("Entering negative number returns -1")
        void RESP_08_test_5(){
            Scanner input = new Scanner("-5");
            int result = libraryInterface.promptBookSelection(input, new PrintWriter(output));
            assertEquals(-1, result);
        }
    }

    @Nested
    @DisplayName("RESP-27: display already is borrower/holder error")
    public class DisplayAlreadyIsBorrowerHolder{

        @Test
        @DisplayName("Display 'You already have a hold on this book'")
        void RESP_27_test_1(){
            libraryInterface.displayAlreadyIsHolder(new PrintWriter(output));
            assertTrue(output.toString().contains("You already have a hold on this book"));
        }

        @Test
        @DisplayName("Display 'You already have this book checked out'")
        void RESP_27_test_2(){
            libraryInterface.displayAlreadyIsBorrower(new PrintWriter(output));
            assertTrue(output.toString().contains("You already have this book checked out"));
        }
    }

    @Nested
    @DisplayName("RESP-29: display a book for return")
    public class DisplayBookForReturn{
        Library library;

        @BeforeEach
        void initLibrary(){
            library = new Library();

            library.setBorrower("Great Gatsby", "squeex");
            library.updateDueDateFromDate("Great Gatsby", new LibraryDate(2025, Calendar.OCTOBER, 14));
            library.setBorrower("The Handmaid's Tale", "squeex");
            library.updateDueDateFromDate("The Handmaid's Tale", new LibraryDate(2025, Calendar.NOVEMBER, 6));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Great Gatsby by F. Scott FitzGerald", "due: 2025-10-28"})
        @DisplayName("Displays title, author, and due date for book 'Great Gatsby'")
        void RESP_29_test_1(String expected){
            Book book = library.getBook("Great Gatsby");
            libraryInterface.displayReturnBook(new PrintWriter(output), book);
            String result = output.toString();

            assertTrue(result.contains(expected), result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"The Handmaid's Tale by Margaret Atwood", "due: 2025-11-20"})
        @DisplayName("Displays title, author, and due date for book 'The Handmaid's Tale'")
        void RESP_29_test_2(String expected){
            Book book = library.getBook("The Handmaid's Tale");
            libraryInterface.displayReturnBook(new PrintWriter(output), book);
            String result = output.toString();

            assertTrue(result.contains(expected), result);
        }
    }

    @Nested
    @DisplayName("RESP-31: prompt offer to place a hold")
    public class PromptPlaceHold{
        final Scanner input = new Scanner("y");

        @Test
        @DisplayName("If user is at borrow limit, displays 'You are at the borrow limit and can't check out another book. Do you want to place a hold?'")
        void RESP_31_test_1(){
            libraryInterface.promptOfferHold(input, new PrintWriter(output), TransactionEnum.AT_BORROWING_LIMIT);
            String result = output.toString();

            assertTrue(result.contains("You are at the borrow limit and can't check out another book. Do you want to place a hold?"), result);
        }

        @Test
        @DisplayName("If book is checked out, displays 'This book is checked out by another user. Do you want to place a hold?'")
        void RESP_31_test_2(){
            libraryInterface.promptOfferHold(input, new PrintWriter(output), TransactionEnum.CHECKED_OUT_BY_ANOTHER);
            String result = output.toString();

            assertTrue(result.contains("This book is checked out by another user. Do you want to place a hold?"), result);
        }

        @Test
        @DisplayName("If book is on hold, displays 'This book is on hold by another user. Do you want to place a hold?'")
        void RESP_31_test_3(){
            libraryInterface.promptOfferHold(input, new PrintWriter(output), TransactionEnum.ON_HOLD_BY_ANOTHER);
            String result = output.toString();

            assertTrue(result.contains("This book is on hold by another user. Do you want to place a hold?"), result);
        }

        @ParameterizedTest
        @EnumSource(value = TransactionEnum.class, names = {"CHECKED_OUT_BY_USER", "ON_HOLD_BY_USER", "AT_HOLD_LIMIT", "CAN_BORROW", "CAN_HOLD"})
        @DisplayName("Other TransactionEnum inputs throws IllegalArgumentException")
        void RESP_31_test_4(TransactionEnum transactionEnum){
            assertThrows(IllegalArgumentException.class, () -> libraryInterface.promptOfferHold(input, new PrintWriter(output), transactionEnum));
        }
    }

    @Nested
    @DisplayName("RESP-32: prompt user acknowledgement")
    public class PromptAcknowledgement{
        @Test
        @DisplayName("Displays: 'The Apothecary Diaries: Volume 1' has been borrowed and is due on 2025-10-29")
        void RESP_32_test_1(){
            Scanner input = new Scanner("Any input");

            libraryInterface.promptAcknowledgement(input, new PrintWriter(output), "'The Apothecary Diaries: Volume 1' has been borrowed and is due on 2025-10-29");
            String result = output.toString();
            assertTrue(output.toString().contains("'The Apothecary Diaries: Volume 1' has been borrowed and is due on 2025-10-29"));
        }
    }
}

