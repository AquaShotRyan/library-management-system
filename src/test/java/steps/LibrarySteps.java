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
    String curUser;

    @Given("the library is initialized with books and users")
    public void init_library(){
        library = new Library();
    }

    @Given("today is 2025-09-20")
    public void set_today(){
        today = new LibraryDate(2025, Calendar.SEPTEMBER, 20);
    }

    @Given("I'm not logged in")
    public void not_logged_in(){
        curUser = null;
        library.logout();
    }

    @Given("{string} has no borrower")
    public void remove_book_borrower(String bookTitle){
        Book book = library.getBook(bookTitle);
        book.removeCurBorrower();
    }

    @Given("{string} has no holders")
    public void remove_book_holders(String bookTitle){
        Book book = library.getBook(bookTitle);
        book.removeCurHolder();
        User holderInQueue = book.popHolder();
        while (holderInQueue != null){
            holderInQueue = book.popHolder();
        }
    }

    @Given("I'm logged in as {string}")
    public void logged_in_as(String username){
        login_as(username);
    }

    @Given("{string} checked out {string}")
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

    @When("I login as {string}")
    public void login_as(String username){
        for (UserData user: UserData.values()){
            if (username.equals(user.getUsername())) {
                library.login(username, user.getPassword());
                curUser = username;
            }
        }
    }
    @When("I check out {string}")
    public void cur_user_check_out_book(String bookTitle){

        check_out_book(curUser, bookTitle);
    }

    @When("I log out")
    public void logout(){
        library.logout();
        curUser = null;
    }

    @When("I return {string}")
    public void cur_user_return_book(String bookTitle){
        library.removeBorrower(bookTitle, curUser);
    }

    @Then("I should be logged in as {string}")
    public void is_logged_in_as(String username){
        String result = library.getSessionUsername();
        assertEquals(username, result);
    }

    @Then("I should see {string} is {string}")
    public void book_is_available(String bookTitle, String availabilityStr){
        AvailabilityEnum expected = AvailabilityEnum.getAvailablilityEnumFromStr(availabilityStr);
        if (expected == null)
            fail(String.format("'%s' is an invalid status", availabilityStr));

        Book book = library.getBook(bookTitle);
        AvailabilityEnum result = book.getAvailabilityStatus(curUser);

        assertEquals(expected, result);
    }

    @Then("I should see {string} has author {string}")
    public void book_has_author(String bookTitle, String bookAuthor){
        Book book = library.getBook(bookTitle);
        assertEquals(bookAuthor, book.getAuthor());
    }

    @Then("I should see my current book count is {int}")
    public void display_book_count(int expectedBookCount){
        int result = library.getBorrowedBooksNum(curUser);
        assertEquals(expectedBookCount, result);
    }

    @Then("I should get no notification about a held being available")
    public void no_held_book_notification(){
        assertFalse(library.heldBookIsAvailable(curUser));
    }

    @Then("{string} should be the current borrower of {string}")
    public void user_is_borrower_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertEquals(username, book.getCurBorrower().getUsername(), "Instead: "+book.getCurBorrower().getUsername());
        assertTrue(library.borrowerHasBook(bookTitle, username));
    }

    @Then("I am logged out")
    public void user_is_logged_out(){
        assertNull(library.getSessionUsername());
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

    /* ========= 2. multiple_holds_queue_processing ========= */
    @Given("{string} places a hold on {string}")
    public void user_places_hold_on_book(String username, String bookTitle){
        library.setHolder(bookTitle, username);
    }

    @Given("{string} is the current holder of {string}")
    public void user_is_holder_of_book(String username, String bookTitle){
        user_places_hold_on_book(username, bookTitle);
    }

    @When("{string} returns {string}")
    public void user_returns_book(String username, String bookTitle){
        library.removeBorrower(bookTitle, username);
    }

    @When("{string} checks out {string}")
    public void user_checks_out_book(String username, String bookTitle){
        check_out_book(username, bookTitle);
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

    @Then("{string} should be first in the hold queue of {string}")
    public void should_be_first_in_queue(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertEquals(username, book.peekHolderQueue().getUsername());
    }

    @When("I place a hold on {string}")
    public void I_place_hold_on_book(String bookTitle){
        user_places_hold_on_book(curUser, bookTitle);
    }

    @Then("{string} should have {int} books")
    public void should_have_3_books(String username, int bookCount){
        int result = library.getBorrowedBooksNum(username);
        assertEquals(bookCount, result);
    }

    @Then("I should get offered to place a hold for {string}")
    public void should_get_offer_to_hold(String bookTitle){
        TransactionEnum result = library.verifyHolding(bookTitle, curUser);
        assertNotEquals(TransactionEnum.CAN_BORROW, result);
        assertNotEquals(TransactionEnum.CHECKED_OUT_BY_USER, result);
    }

    @Then("I should get {string} that my held book is available")
    public void I_should_get_notification_held_book_available(String isNotified){
        boolean result = library.heldBookIsAvailable(curUser);
        if (isNotified.equals("a notification"))
            assertTrue(result);
        else
            assertFalse(result);
    }
}
