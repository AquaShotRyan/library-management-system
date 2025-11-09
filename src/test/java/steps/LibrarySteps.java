package steps;

import org.library.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;


import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {
    private Library library;
    LibraryDate today;

    @Given("the library is initialized with books and users")
    public void init_library(){
        library = new Library();
    }

    @Given("today is 2025-09-20")
    public void set_today(){
        today = new LibraryDate(2025, Calendar.SEPTEMBER, 20);
    }

    @Given("nobody is logged in")
    @When("they log out")
    public void logout(){
        library.logout();
    }

    @Given("{string} checked out {string}")
    @When("{string} checks out {string}")
    public void check_out_book(String username, String bookTitle){
        TransactionEnum borrowValidation = library.verifyBorrowing(bookTitle, username);
        if (borrowValidation == TransactionEnum.CAN_BORROW){
            library.checkoutBook(bookTitle, username, today);
        }
    }

    @Given("{string} borrowed and returned {string}")
    public void borrow_and_return_book(String username, String bookTitle){
        check_out_book(username, bookTitle);
        library.removeBorrower(bookTitle, username);
    }

    @Given("{string} is logged in")
    @When("{string} logs in")
    @When("{string} logs in and selects to borrow a book")
    public void login_as(String username){
        for (UserData user: UserData.values()){
            if (username.equals(user.getUsername())) {
                library.login(username, user.getPassword());
            }
        }
    }

    @Then("{string} should be logged in")
    public void is_logged_in_as(String username){
        String result = library.getSessionUsername();
        assertEquals(username, result);
    }

    @Then("{string} should see {string} is {string}")
    public void see_availability_status(String username, String bookTitle, String availabilityStr){
        AvailabilityEnum expected = AvailabilityEnum.getAvailablilityEnumFromStr(availabilityStr);
        if (expected == null)
            fail(String.format("'%s' is an invalid status", availabilityStr));

        Book book = library.getBook(bookTitle);
        AvailabilityEnum result = book.getAvailabilityStatus(username);

        assertEquals(expected, result);
    }

    @Then("{string} should get no notification about a held book being available")
    public void no_held_book_notification(String username){
        assertFalse(library.heldBookIsAvailable(username));
    }

    @Then("{string} should be the current borrower of {string}")
    public void user_is_borrower_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertEquals(username, book.getCurBorrower().getUsername(), "Instead: "+book.getCurBorrower().getUsername());
        assertTrue(library.borrowerHasBook(bookTitle, username));
    }

    @Then("{string} should be logged out")
    public void user_is_logged_out(String username){
        assertFalse(library.userIsLoggedIn(username));
    }

    @Then("{string} is due on {string}")
    public void check_book_due_date(String bookTitle, String dueDate){
        Book book = library.getBook(bookTitle);
        assertEquals(dueDate, book.getDueDateStr());
    }

    @Then("{string} should NOT be the current borrower of {string}")
    public void user_is_not_borrower_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertFalse(library.borrowerHasBook(bookTitle, username));
        assertFalse(book.curBorrowerIs(username));
    }

    @Given("{string} places a hold on {string}")
    @Given("{string} is the current holder of {string}")
    public void user_places_hold_on_book(String username, String bookTitle){
        TransactionEnum verifyHolding = library.verifyHolding(bookTitle, username);
        if (verifyHolding == TransactionEnum.CAN_HOLD){
            library.placeHold(bookTitle, username);
        }
    }

    @Given("{string} returned {string}")
    @When("{string} returns {string}")
    public void user_returns_book(String username, String bookTitle){
        library.removeBorrower(bookTitle, username);
    }

    @Then("{string} should be the current holder of {string}")
    public void user_should_be_holder_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertEquals(username, book.getCurHolder().getUsername());
    }

    @Then("{string} should get a notification that their held book is available")
    public void should_get_available_notification(String username){
        assertTrue(library.heldBookIsAvailable(username));
    }

    @Then("{string} should NOT get a notification that their held book is available")
    public void should_not_get_available_notification(String username){
        assertFalse(library.heldBookIsAvailable(username));
    }

    @Then("{string} should have {int} books")
    @Then("{string} should see their current book count is {int}")
    public void check_book_count(String username, int bookCount){
        int result = library.getBorrowedBooksNum(username);
        assertEquals(bookCount, result);
    }

    @Then("{string} should get offered to place a hold for {string}")
    public void should_get_offer_to_hold(String username, String bookTitle){
        TransactionEnum result = library.verifyHolding(bookTitle, username);
        assertNotEquals(TransactionEnum.CAN_BORROW, result);
        assertNotEquals(TransactionEnum.CHECKED_OUT_BY_USER, result);
    }

    @Then("{string} should get {string} that my held book is available")
    public void I_should_get_notification_held_book_available(String username, String isNotified){
        boolean result = library.heldBookIsAvailable(username);
        if (isNotified.equals("a notification"))
            assertTrue(result);
        else
            assertFalse(result);
    }

    @Then("there should be no holders for {string}")
    public void no_holder_for_book(String bookTitle){
        Book book = library.getBook(bookTitle);
        assertEquals(0, book.getHoldersNum());
    }

    @When("they check out {string}")
    public void they_check_out_book(String bookTitle){
        check_out_book(library.getSessionUsername(), bookTitle);
    }

    @When("they return {string}")
    public void they_return_book(String bookTitle){
        user_returns_book(library.getSessionUsername(), bookTitle);
    }

    @Then("they should see {string} is {string}")
    @Then("should see {string} is {string}")
    @Then("see {string} is {string}")
    public void should_see_book_availability(String bookTitle, String availability){
        see_availability_status(library.getSessionUsername(), bookTitle, availability);
    }

    @Then("they should see their current book count is {int}")
    @Then("should see their current book count is {int}")
    @Then("see their current book count is {int}")
    public void should_see_book_count(int bookCount){
        check_book_count(library.getSessionUsername(), bookCount);
    }

    @Then("should get no notification about a held book being available")
    @Then("get no notification about a held book being available")
    public void should_get_no_held_book_notification(){
        no_held_book_notification(library.getSessionUsername());
    }


}
