const URL = "http://localhost:3000";
const ALICE = "alice";
const pass1 = "pass123";
const BOB = "bob";
const pass2 = "pass456";
const CHARLIE = "charlie";
const pass3 = "pass789";
const dueDate = "2025-12-09"; // today is set to 2025-11-25 api-router.js

describe("Library Book Management", () => {
  beforeEach(() => {
    // Reset the library
    cy.request("DELETE", URL+"/api/reset");
    cy.visit(URL);
  });

  it("A1_scenario", () => {
    /* ========= 'alice' borrows 'The Great Gatsby' ========= */
    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert user sees they are currently borrowing 0 books
    /* 
      Relevant to verifying that a borrow was successful
      when this later shows as borrowing 1 book 
    */
    cy.assert_books_num(0);

    // Create alias for book card (cards that a user sees when browsing books) of 'The Great Gatsby'
    cy.get('[test-id="the-great-gatsby"]').as("the_great_gatsby");

    cy.get("@the_great_gatsby").within(($bookcard) => {
      // Assert 'The Great Gatsby' displays as 'Available'
      /* 
        Relevant to verifying that a borrow will change
        book status
      */
      cy.assert_availability("Available");
    });

    // Click 'Checkout' button for book: 'The Great Gatsby'
    cy.click_check_out_of("the-great-gatsby");

    // Click 'Yes' button to confirm borrowing
    // Will be redirected to the menu
    cy.click_borrow_yes();

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert user sees they are currently borrowing 1 books
    /* 
      Relevant to verifying borrowing of an 'Available' book
      was successful
    */
    cy.assert_books_num(1);

    cy.get("@the_great_gatsby").within(($bookcard) => {
      // Assert 'The Great Gatsby' status is 'Checked Out'
      /* 
        Relevant to verifying that the status changes
        after a borrow
      */
      cy.assert_availability("Checked Out");
    });

    // Go back to menu and click "Logout"
    cy.go("back");
    cy.logout();

    /* ========= bob logs in and see status of 'The Great Gatsby' ========= */
    // Login as 'bob'
    cy.login(BOB, pass2);

    // Navigate to Borrow page
    cy.select_borrow();

    cy.get("@the_great_gatsby").within(($bookcard) => {
      // Assert that 'The Great Gatsby' is 'Checked Out'
      /* 
        Relevant to checking the status changes after a borrow
        and remains the same after another user logs in
      */
      cy.assert_availability("Checked Out");
    });

    /* ========= bob attempts to borrow 'The Great Gatsby' and fails ========= */
    // Click 'Checkout' for book: 'The Great Gatsby'
    cy.click_check_out_of("the-great-gatsby");

    // Click 'Yes' button to confirm borrowing
    // User will be redirected to another page that offers a hold
    cy.click_borrow_yes();

    // Assert user sees the 'Place a hold' page
    /*
      Relevant to checking that the borrow fails as
      the system offers a hold if the book is
      unavailable
    */
    cy.get("#place-hold-msg").should("contain.text", "The Great Gatsby is currently unavailable, would you like to place a hold?");

    // User denies hold and gets redirected to the menu
    cy.click_no();

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert bob is borrowing 0 books
    /* 
      Relevant to checking borrow fails if book is unavailable
    */
    cy.assert_books_num(0);

    // Go back to menu
    cy.go("back");

    // Assert bob is prevented from returning books (because he has none)
    /* 
      Relevant to checking borrow failed by being prevented to return
      books (since 'bob' has none)
    */
    cy.assert_return_disabled();

    cy.logout();

    /* ========= alice returns 'The Great Gatsby' ========= */
    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Navigate to 'Return a book' page
    cy.select_return();

    // Assert "The Great Gatsby" is displayed as a book to return
    /* 
      Relevant to verifying alice's earlier borrow of an 'Available'
      book was successful
    */
    cy.get("@the_great_gatsby").should("exist");

    // Click 'Return' on 'The Great Gatsby'
    cy.get("@the_great_gatsby").contains("Return").click();

    // Navigate to Borrow page
    cy.select_borrow();

    cy.get("@the_great_gatsby").within(($bookcard) => {
      // Assert 'The Great Gatsby' displays as 'Available'
      /* 
        Relevant to verifying that status changes after
        return
      */
      cy.assert_availability("Available");
    });

    // Go back to menu and logout
    cy.go("back");
    cy.logout();

    /* ========= bob sees status of 'The Great Gatsby' ========= */
    // Login as 'bob'
    cy.login(BOB, pass2);

    // Navigate to Borrow page
    cy.select_borrow();

    cy.get("@the_great_gatsby").within(($bookcard) => {
      // Assert that 'The Great Gatsby' is 'Available'
      /* 
        Relevant to checking the status reflects the return
        and is consistent for other users
      */
      cy.assert_availability("Available");
    });
  });

  it("multiple_holds_queue_processing", () => {
    
    /* ========= Setup: 'charlie' borrows '1984' ========= */
    // Login as 'charlie'
    cy.login(CHARLIE, pass3);

    // 'charlie' borrows '1984'
    cy.borrow_book("1984");

    /* ========== Setup: 'bob' and 'alice' join the hold-queue for '1984' ======== */ 
    // Logout and login as 'bob'
    cy.logout();
    cy.login(BOB, pass2);

    // Place hold on '1984'
    cy.hold_book("1984");

    // Logout and login as 'alice'
    cy.logout();
    cy.login(ALICE, pass1);

    // Place hold on '1984'
    cy.hold_book("1984");

    /* ========= Assert queue order of '1984' and 'charlie' returns '1984' ========= */
    // Logout and login as 'charlie'
    cy.logout();
    cy.login(CHARLIE, pass3);

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert queue order of '1984' is '[bob,alice]' (bob first, then alice second)
    /* 
      Relevant for verifying that queue is FIFO
    */
    cy.assert_hold_queue("1984", "bob", "alice");

    // Go back to the menu
    cy.go("back");

    // Return '1984'
    cy.return_book("1984");

    /* ========= 'alice' attempts to borrow '1984' and fails ======== */

    // Logout and login as 'alice'
    cy.logout();
    cy.login(ALICE, pass1)

    // Assert alice doesn't get a notification
    /*
      Relevant for showing that a holder that doesn't get notified
      (not first in queue) cannot borrow the book they're holding
    */
    cy.get("#notification-msg").should("have.text", "Notification: N/A");

    // Navigate to Borrow page
    cy.select_borrow();

    // Attempt to borrow '1984'
    cy.click_check_out_of("1984");
    cy.click_borrow_yes();
    cy.click_hold_yes(); // Gets offer to place hold and clicks yes, but gets denied because alice is already a holder

    // Assert they are unable to navigate to Return page due to alice having no books
    /* 
      Shows that a holder that doesn't get a notification cannot
      borrow the book
    */
    cy.assert_return_disabled();

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert alice has 0 books
    /* 
      Again, asserting that the borrow wasn't successful to show
      that an unnotfied holder cannot borrow the book
    */
    cy.assert_books_num(0);

    // Assert alice is stil in the hold queue
    /*
      Again, asserting that the borrow wasn't successful
      since they're still holding the book
    */
    cy.assert_hold_queue("1984", "bob", "alice");

    // Go back to menu and logout
    cy.go("back");
    cy.logout();

    // Login as 'bob'
    cy.login(BOB, pass2);

    // Assert notification that 1984 is available (after logging in)
    /* 
      Relevant to showing the notification is sent to the correct user 
      (the first in queue)
    */
    cy.get("#notification-msg").should("have.text", "Notification: 1984 is now available!");

    // Assert bob is not borrowing any books
    /*
      Relevant to queue advancement after a return, as
      bob should have to explcitily borrow the book
    */
    cy.assert_return_disabled();

    // Borrow '1984'
    cy.borrow_book("1984");

    /* ========= Assert 'bob' (notified) is the borrower of '1984' ========= */

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert bob is borrowing 1 book
    /*
      Relevant to verifying that the borrow was successful
      for the notified holder
    */
    cy.assert_books_num(1);

    // Assert hold queue only has alice
    /* 
      Relevant to verifying that the queue advances
      properly after bob (first in queue) borrowed the book
    */
    cy.assert_hold_queue("1984", "alice");

    // Go back to menu
    cy.go("back");

    // Navigate to Return page
    cy.select_return();

    // Assert '1984' appears in Return page
    /*
      Relevant for verifying success of borrow
      for the notified holder (bob)
    */
    cy.get('[test-id="1984"]').should("exist");
  });

  it("borrowing_limit_and_hold_interactions", () => {
    // Login as 'bob' and borrow 'To Kill a Mockingbird'
    cy.login(BOB, pass2);
    cy.borrow_book("to-kill-a-mockingbird");

    // Logout
    cy.logout();

    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Borrow 3 books to reach borrow-limit
    cy.borrow_book("the-hobbit");
    cy.borrow_book("pride-and-prejudice");
    cy.borrow_book("the-catcher-in-the-rye");

    // Assert 'alice' is at the limit (3 books)
    /* 
      Relevant to when it's later verified that
      'alice' doesn't exceed the limit when they
      attempt to borrow a 4th book
    */
    cy.select_borrow();
    cy.assert_books_num(3);

    // Attempt to borrow a 4th book: 'To Kill a Mockingbird'
    cy.click_check_out_of("to-kill-a-mockingbird");
    cy.click_borrow_yes(); // Redirects to hold confirmation page

    // Assert borrow limit message
    /*
      Relevant to verifying that the system prevents
      borrowing beyond the limit
    */
    cy.get("#place-hold-msg").should("have.text", "You are at the 3-book limit, would you like to place a hold on To Kill a Mockingbird?");

    // Place a hold on 'To Kill a Mockingbird'
    cy.click_hold_yes();

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert alice is in the hold queue
    /*
      Relevant for verifying that a user at the limit
      can still place a hold
    */
    cy.assert_hold_queue("to-kill-a-mockingbird", "alice");

    // Assert alice has 3 books (rather than 4)
    /*
      Again, relevant to verifying that 'alice' can't
      borrow beyond the limit
    */
    cy.assert_books_num(3);

    // Go back the menu
    cy.go("back");

    // Logout
    cy.logout();

    // Login as 'bob'
    cy.login(BOB, pass2);

    // Return 'To Kill a Mockingbird'
    cy.return_book("to-kill-a-mockingbird");

    // Logout
    cy.logout();

    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Assert notification for 'To Kill a Mockingbird'
    /*
      Relevant to verifying that a user at the
      3-book limit can still get notified about
      their hold
    */
    cy.get("#notification-msg").should("have.text", "Notification: To Kill a Mockingbird is now available!");

    // Attempt to borrow 'To Kill a Mockingbird'
    cy.select_borrow();
    cy.click_check_out_of("to-kill-a-mockingbird");
    cy.click_borrow_yes();
    cy.click_hold_yes(); // Will offer a hold, but will get denied since alice is already a holder

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert that 'alice' is still borrowing 3 books
    /*
      Relevant to verifying 3-book limit is enforced, even
      if she was notified
    */
    cy.assert_books_num(3);

    // Go back to menu
    cy.go("back");

    // Navigate to Return page
    cy.select_return();

    // Assert 'To Kill a Mockingbird' is not in return list
    /*
      Relevant to verifying 3-book limit is enforced, even
      if she was notified
    */
    cy.get('[test-id="to-kill-a-mockingbird"]').should("not.exist");

    // Return one book to gain capacity
    cy.get('[test-id="the-hobbit"]').contains("Return").click();

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert 'alice' has 2 books (gained borrowing capacity)
    /*
      Relevant to verifying gained capacity, she should be able
      to borrow a book because she has < 3 books
    */
    cy.assert_books_num(2);

    // Attempt to borrow 'To Kill a Mockingbird'
    cy.click_check_out_of("to-kill-a-mockingbird");
    cy.click_borrow_yes();

    // Assert 'alice' is now borrowing 'To Kill a Mockingbird'
    /*
      Relevant to verifying gained capacity after returning
      a book, as she succeded to borrowing 'To Kill a Mockingbird'
    */
    cy.select_return();
    cy.get('[test-id="to-kill-a-mockingbird"]').should("exist");

    // Go back to menu
    cy.go("back");

    // Navigate to Borrow page
    cy.select_borrow();

    // Assert 'alice' is borrowing 3 books
    /*
      Relevant to verifying that alice was
      able to borrow a book after gaining
      capacity
    */
    cy.assert_books_num(3);
  });
});
