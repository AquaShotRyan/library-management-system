package org.library;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptanceTest {

    void clearBuffer(StringWriter output){
        output.getBuffer().setLength(0);
    }

    @Test
    @DisplayName("A-TEST-01: Multi-User Borrow and Return with Availability Validated")
    void A_TEST_01(){
        final String USER1_NAME = UserData.RYAN.getUsername();
        final String USER1_PASS = UserData.RYAN.getPassword();
        final String USER2_NAME = UserData.SQUEEX.getUsername();
        final String USER2_PASS = UserData.SQUEEX.getPassword();
        final String BOOK1_TITLE = BookData.GREAT_GATSBY.getTitle();

        LibraryDate dueDate = new LibraryDate(Calendar.getInstance()); // add 14 days from today
        dueDate.addDays(Library.BORROWING_DAY_LENGTH);

        // ARRANGE: setup library controller
        Library library = new Library();
        LibraryInterface ui = new LibraryInterface();
        StringWriter output = new StringWriter();
        LibraryController controller = new LibraryController(library, ui, new PrintWriter(output));

        Book greatGatsby = library.getBook(BOOK1_TITLE);

        // --- UC-01: Login as User 1 (verify credentials prompt and user session) ----
        String enterUsernamePassword = USER1_NAME+"\n"+USER1_PASS+"\n";
        controller.promptLogin(new Scanner(enterUsernamePassword));
        assertAll("UC-01: check successful login, prompting of credentials, and session was created for user1",
                () -> assertTrue(output.toString().contains("username:")),
                () -> assertTrue(output.toString().contains("password:")),
                () -> assertEquals(USER1_NAME, library.getSessionUsername())
        );

        // --- UC-01: User 1 doesn't get notification of available books ---
        controller.notifyHeldBookAvailability(USER1_NAME);
        assertFalse(output.toString().contains("NOTIFICATION"));

        // --- UC-01: User 1 sees menu options (verify options are displayed) ---
        MenuEnum menuInput = controller.promptMenu(new Scanner("1\n"));
        assertAll("UC-02: user sees menu options",
                () -> assertTrue(output.toString().contains("1) Borrow a book")),
                () -> assertTrue(output.toString().contains("2) Return a book")),
                () -> assertTrue(output.toString().contains("3) Logout"))
        );

        // --- UC-02: User 1 sees they have 0 borrowed books ---
        controller.displayNumberOfBorrowedBooks();
        assertTrue(output.toString().contains("Current number of borrowed books: 0"), output.toString());

        // get index of 'The Great Gatsby'
        List<BookData> orderedBooks = BookData.getArrSortedByAuthor();
        int gatsbyIndex = IntStream.range(0, orderedBooks.size()).filter(i -> greatGatsby.getTitle().equals(orderedBooks.get(i).getTitle())).findFirst().orElse(0);

        // --- UC-02: User 1 sees 20 books, including Great Gatsby (verify multiple books are displayed and user is prompted to select a book) ---
        controller.promptBorrowBook(new Scanner("5\n"));
        assertAll("UC-02: user sees display of all available books",
                () -> assertTrue(output.toString().contains("2001: A Space Odyssey by Arthur C. Clarke")),
                () -> assertTrue(output.toString().contains("Great Gatsby by F. Scott FitzGerald")),
                () -> assertTrue(output.toString().contains("Supergirl: Woman of Tomorrow #1 by Tom King")),
                () -> assertFalse(output.toString().contains("Checked Out")), // should not see any book as Checked Out
                () -> assertFalse(output.toString().contains("On Hold")), // should not see any book as On Hold
                () -> assertTrue(output.toString().contains("Enter a book number:")),
                () -> assertEquals(greatGatsby, library.getAllBooks().get(gatsbyIndex)) // Great Gatsby at index 5
        );

        // --- UC-02: User 1 sees confirmation message with borrowing details ---
        String askConfirmationStr = String.format("Do you want to borrow Great Gatsby and return it on %s? (y/n)", dueDate.toString());
        controller.askBookConfirmation(new Scanner("y\n"), BOOK1_TITLE);
        assertTrue(output.toString().contains(askConfirmationStr));

        // update system to reflect borrowing Great Gatsby
        library.setBorrower("Great Gatsby", USER1_NAME);
        LibraryDate today = new LibraryDate(Calendar.getInstance());
        library.updateDueDateFromDate(BOOK1_TITLE, today);
        library.addBorrowTransaction(new BorrowTransaction(BOOK1_TITLE, USER1_NAME, today.toString()));
        assertAll("UC-02: confirm library state after borrow",
                () -> assertEquals(1, library.getSessionBorrowedBooksNum()), // borrower has 1 borrowed book
                () -> assertEquals(USER1_NAME, greatGatsby.getCurBorrower().getUsername()), // current borrower of book is user 1
                () -> assertNull(greatGatsby.getCurHolder()), // book has no holders
                () -> assertNull(greatGatsby.peekHolderQueue()),
                () -> assertEquals(greatGatsby, library.getBorrowedBooks(USER1_NAME).getBookByTitle(BOOK1_TITLE)), // borrower has Great Gatsby in his borrowed books collection
                () -> assertEquals(dueDate, greatGatsby.getDueDate()) // due date of Great Gatsby is 14 days from today
        );

        // --- UC-02: User 1 acknowledges transaction (verify confirmation message) ---
        String borrowingConfirmationStr = String.format("'Great Gatsby' has been borrowed and is due on %s", dueDate);
        controller.confirmBookCheckOut(new Scanner("\n"), BOOK1_TITLE);
        assertTrue(output.toString().contains(borrowingConfirmationStr));


        // --- UC-04: User 1 logs out (verify session and logout confirmation message) ----
        controller.logout(new Scanner("y\n"));
        assertAll("UC-04: user gets asked if they want to logout and enters 'y' for yes",
                () -> assertTrue(output.toString().contains("Are you sure you want to log out? (y/n):")),
                () -> assertNull(library.getSessionUsername())
        );

        clearBuffer(output);

        // --- UC-01: Login as User 2 ----
        enterUsernamePassword = USER2_NAME+"\n"+USER2_PASS+"\n";
        controller.promptLogin(new Scanner(enterUsernamePassword));
        assertEquals(USER2_NAME, library.getSessionUsername());

        // --- UC-01: User 2 doesn't get notification of available books ---
        controller.notifyHeldBookAvailability(USER2_NAME);
        assertFalse(output.toString().contains("NOTIFICATION"));

        // --- UC-02: User 2 sees they have 0 borrowed books after selecting to borrow a book ---
        controller.displayNumberOfBorrowedBooks();
        assertTrue(output.toString().contains("Current number of borrowed books: 0"), output.toString());

        // --- UC-02: User 2 sees Great Gatsby is checked out, enters -1 to not borrow or place hold on any book ---
        controller.promptBorrowBook(new Scanner("-1"));
        assertAll("UC-02: check Great Gatsby is checked out",
                () -> assertTrue(output.toString().contains("Checked Out")), // Great Gatsby would display this
                () -> assertFalse(output.toString().contains("On Hold")),
                () -> assertEquals(AvailabilityEnum.CHECKED_OUT, greatGatsby.getAvailabilityStatus(USER2_NAME))
        );

        // --- UC-04: User 2 logs out ---
        controller.logout(new Scanner("y\n"));
        assertNull(library.getSessionUsername());

        clearBuffer(output);

        // --- UC-01: User 1 logs back in ---
        enterUsernamePassword = USER1_NAME+"\n"+USER1_PASS+"\n";
        controller.promptLogin(new Scanner(enterUsernamePassword));
        assertAll("UC-01: check user is in session and library state did not change",
                () -> assertEquals(USER1_NAME, library.getSessionUsername()),
                () -> assertEquals(1, library.getSessionBorrowedBooksNum()),
                () -> assertEquals(USER1_NAME, greatGatsby.getCurBorrower().getUsername()),
                () -> assertNull(greatGatsby.getCurHolder()),
                () -> assertNull(greatGatsby.peekHolderQueue()),
                () -> assertEquals(greatGatsby, library.getBorrowedBooks(USER1_NAME).getBookByTitle(BOOK1_TITLE)),
                () -> assertEquals(dueDate, greatGatsby.getDueDate())
        );

        // --- UC-01: User 1 doesn't get notification of available books ---
        controller.notifyHeldBookAvailability(USER1_NAME);
        assertFalse(output.toString().contains("NOTIFICATION"));

        // --- UC-03: User 1 selects to return book and selects Great Gatsby (0) to return ---
        Book selectedBook = controller.promptReturnBook(new Scanner("0\n\n"), USER1_NAME);
        assertAll("UC-03: User 1 selects to return book and sees Great Gatsby as his 0th borrowed book",
                () -> assertTrue(output.toString().contains("0")),
                () -> assertTrue(output.toString().contains("Great Gatsby by F. Scott FitzGerald")),
                () -> assertTrue(output.toString().contains("due: "+library.getBook(BOOK1_TITLE).getDueDateStr())),
                () -> assertTrue(output.toString().contains("Enter a book number:"))
        );

        // update system to reflect returning Great Gatsby
        library.removeBorrower(BOOK1_TITLE, USER1_NAME);
        assertAll("UC-03: confirm library state after returning Great Gatsby",
                () -> assertEquals(0, library.getSessionBorrowedBooksNum()),
                () -> assertNull(greatGatsby.getCurBorrower()),
                () -> assertNull(greatGatsby.getCurHolder()),
                () -> assertNull(greatGatsby.getDueDate()),
                () -> assertNull(greatGatsby.peekHolderQueue()),
                () -> assertNull(library.getBorrowedBooks(USER1_NAME).getBookByTitle(BOOK1_TITLE))
        );

        // --- UC-03: User 1 sees confirmation message of return ---
        controller.confirmBookReturn(new Scanner("\n"), BOOK1_TITLE);
        assertTrue(output.toString().contains("'Great Gatsby' has been returned"));

        // --- UC-04: User 1 logs out again  ---
        controller.logout(new Scanner("y\n"));
        assertNull(library.getSessionUsername());

        clearBuffer(output);

        // --- UC-01: User 2 logs in again ---
        enterUsernamePassword = USER2_NAME+"\n"+USER2_PASS+"\n";
        controller.promptLogin(new Scanner(enterUsernamePassword));
        assertEquals(USER2_NAME, library.getSessionUsername());

        // --- UC-01: User 2 doesn't get notification of available books ---
        controller.notifyHeldBookAvailability(USER1_NAME);
        assertFalse(output.toString().contains("NOTIFICATION"));

        // --- UC-02: User 2 selects to borrow a book and gets notified they have 0 borrowed books ---
        controller.displayNumberOfBorrowedBooks();
        assertTrue(output.toString().contains("Current number of borrowed books: 0"), output.toString());

        // --- UC-02: User 2 sees Great Gatsby is available ('Checked Out' not displayed) ---
        controller.promptBorrowBook(new Scanner("-1"));
        assertAll("UC-02: Check Great Gatsby is still displayed and is Available",
                () -> assertFalse(output.toString().contains("Checked Out")), // all books should display as Available
                () -> assertFalse(output.toString().contains("On Hold")),
                () -> assertTrue(output.toString().contains("Great Gatsby by F. Scott FitzGerald"))
        );
        // --- UC-04: User 2 logs out again ---
        controller.logout(new Scanner("y\n"));
        assertNull(library.getSessionUsername());
    }

    @Test
    @DisplayName("A-TEST-02: Initialization and Authentication with Error Handling")
    void A_TEST_02(){
        final String USER1_NAME = UserData.RYAN.getUsername();
        final String USER1_PASS = UserData.RYAN.getPassword();
        final String USER2_NAME = UserData.GLORP.getUsername();
        final String USER3_NAME = UserData.SQUEEX.getUsername();

        // ARRANGE: setup library controller
        Library library = new Library();
        LibraryInterface ui = new LibraryInterface();
        StringWriter output = new StringWriter();
        LibraryController controller = new LibraryController(library, ui, new PrintWriter(output));

        // --- UC-01: system is initialized with 20 books and 3 borrows
        assertAll("UC-01: system is initialized",
                () -> assertTrue(library.getAllBooks().size() >= 20),
                () -> assertTrue(library.getBorrowersSize() >= 3),
                () -> assertEquals(0, library.getBorrowedBooks(USER1_NAME).size()),
                () -> assertEquals(0, library.getBorrowedBooks(USER2_NAME).size()),
                () -> assertEquals(0, library.getBorrowedBooks(USER3_NAME).size())
        );
        // check all books are seen as Available by all borrowers
        for (Book b: library.getAllBooks()){
            assertEquals(AvailabilityEnum.AVAILABLE, b.getAvailabilityStatus(USER1_NAME));
            assertEquals(AvailabilityEnum.AVAILABLE, b.getAvailabilityStatus(USER2_NAME));
            assertEquals(AvailabilityEnum.AVAILABLE, b.getAvailabilityStatus(USER3_NAME));
        }

        // --- UC-01: Login as User 1 (verify credentials prompt and user session) ----
        controller.promptLogin(new Scanner(USER1_NAME+"\n"+USER1_PASS+"\n"));
        assertAll("UC-01: check successful login, prompting of credentials, and session was created for user1",
                () -> assertTrue(output.toString().contains("username:")),
                () -> assertTrue(output.toString().contains("password:")),
                () -> assertEquals(USER1_NAME, library.getSessionUsername())
        );

        // --- UC-01: User 1 doesn't get notification of available books ---
        controller.notifyHeldBookAvailability(USER1_NAME);
        assertFalse(output.toString().contains("NOTIFICATION"));

        // --- UC-01: User 1 sees menu options (verify options are displayed) ---
        MenuEnum menuInput = controller.promptMenu(new Scanner("3\n"));
        assertAll("UC-02: user sees menu options",
                () -> assertTrue(output.toString().contains("1) Borrow a book")),
                () -> assertTrue(output.toString().contains("2) Return a book")),
                () -> assertTrue(output.toString().contains("3) Logout"))
        );

        // --- UC-04: User 1 logs out (verify session and logout confirmation message) ----
        controller.logout(new Scanner("y\n"));
        assertAll("UC-04: user gets asked if they want to logout and enters 'y' for yes",
                () -> assertTrue(output.toString().contains("Are you sure you want to log out? (y/n):")),
                () -> assertNull(library.getSessionUsername())
        );

        // --- UC-01: User 2 tries to log in and sees an error message
        controller.promptLogin(new Scanner(USER2_NAME+"\nwrong_password\n"));
        assertAll("UC-01: check unsuccessful login and that error message was displayed",
                () -> assertNull(library.getSessionUsername()), // session should still be null
                () -> assertTrue(output.toString().contains("ERROR: credentials not found"))
        );
    }
}
