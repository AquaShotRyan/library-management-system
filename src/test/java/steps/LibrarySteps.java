package steps;

import org.library.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;


import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {
    private Library library;

    @Given("the library is initialized with books and users")
    public void init_library(){
        library = new Library();
    }

    @When("they log out")
    public void logout(){
        library.logout();
    }

    @Given("{string} is logged in")
    @When("{string} logs in( and selects to borrow a book)")
    public void login_as(String username){
        for (UserData user: UserData.values()){
            if (username.equals(user.getUsername())) {
                library.login(username, user.getPassword());
            }
        }
    }

    @Then("{string} should be borrowing {string}")
    public void user_is_borrower_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertEquals(username, book.getCurBorrower().getUsername(), "Instead: "+book.getCurBorrower().getUsername());
        assertTrue(library.borrowerHasBook(bookTitle, username));
    }

    @Then("{string} should NOT be borrowing {string}")
    public void user_is_not_borrower_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertFalse(library.borrowerHasBook(bookTitle, username));
        assertFalse(book.curBorrowerIs(username));
    }

    @Then("{string} should be the current holder of {string}")
    public void user_should_be_holder_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertTrue(book.curHolderIs(username));
    }

    @Then("{string} should NOT be the current holder of {string}")
    public void user_should_not_be_holder_of_book(String username, String bookTitle){
        Book book = library.getBook(bookTitle);
        assertFalse(book.curHolderIs(username));
    }

    @Then("{string} should get offered to place a hold for {string}")
    public void should_get_offer_to_hold(String username, String bookTitle){
        TransactionEnum result = library.verifyHolding(bookTitle, username);
        assertNotEquals(TransactionEnum.CAN_BORROW, result);
        assertNotEquals(TransactionEnum.CHECKED_OUT_BY_USER, result);
    }

    @When("they (attempt to )borrow {string}")
    public void they_check_out_book(String bookTitle){
        String username = library.getSessionUsername();
        TransactionEnum borrowValidation = library.verifyBorrowing(bookTitle, username);
        if (borrowValidation == TransactionEnum.CAN_BORROW){
            library.checkoutBook(bookTitle, username, new LibraryDate(Calendar.getInstance()));
        }
    }

    @When("they return {string}")
    public void they_return_book(String bookTitle){
        String username = library.getSessionUsername();
        library.removeBorrower(bookTitle, username);
    }

    @When("(they )place(s) a hold on {string}")
    public void places_a_hold_on_book(String bookTitle){
        String username = library.getSessionUsername();
        TransactionEnum verifyHolding = library.verifyHolding(bookTitle, username);
        if (verifyHolding == TransactionEnum.CAN_HOLD){
            library.placeHold(bookTitle, username);
        }
    }

    @Then("(they )(should )see {string} is {string}")
    public void should_see_book_availability(String bookTitle, String availability){
        String username = library.getSessionUsername();
        AvailabilityEnum expected = AvailabilityEnum.getAvailablilityEnumFromStr(availability);
        if (expected == null)
            fail(String.format("'%s' is an invalid status", availability));

        Book book = library.getBook(bookTitle);
        AvailabilityEnum result = book.getAvailabilityStatus(username);

        assertEquals(expected, result);
    }

    @Then("(they )(should )NOT get notified that their held book is available")
    public void should_get_no_held_book_notification(){
        String username = library.getSessionUsername();
        assertFalse(library.heldBookIsAvailable(username));
    }

    @Then("(they )(should )get notified that their held book is available")
    public void should_get_held_book_notification(){
        String username = library.getSessionUsername();
        assertTrue(library.heldBookIsAvailable(username));
    }

    @Then("{string} should be in the hold-queue of {string}")
    public void should_be_in_hold_queue(String username, String bookTitle){
        assertTrue(library.userIsInHoldQueue(bookTitle, username));
    }

    @Then("{string} should have borrowing capacity")
    public void should_have_capacity(String username){
        assertFalse(library.isAtBorrowingCapacity(username));
    }
}
