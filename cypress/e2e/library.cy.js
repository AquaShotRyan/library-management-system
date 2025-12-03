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
    cy.request("DELETE", URL + "/api/reset");
    cy.visit(URL);
  });

  it("A1_scenario", () => {
    /* ========= 'alice' borrows 'The Great Gatsby' ========= */
    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Click 'Borrow a book' button
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

    // Click 'Checkout' button for 'The Great Gatsby'
    cy.click_check_out_of("the-great-gatsby");

    // Click 'Yes' button to confirm borrowing
    // Will be redirected to the menu
    cy.click_borrow_yes();

    // Click 'Borrow a book' button
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

    // Go back to menu and click 'Logout' button
    cy.go("back");
    cy.logout();

    /* ========= bob logs in and see status of 'The Great Gatsby' ========= */
    // Login as 'bob'
    cy.login(BOB, pass2);

    // Click 'Borrow a book' button
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
    cy.get("#place-hold-msg").should(
      "contain.text",
      "The Great Gatsby is currently unavailable, would you like to place a hold?"
    );

    // User clicks 'No' to deny hold
    // Gets redirected to the menu
    cy.click_no();

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert 'bob' sees they are currently borrowing 0 books
    /* 
      Relevant to checking borrow fails if book is unavailable
    */
    cy.assert_books_num(0);

    // Go back to menu
    cy.go("back");

    // Assert 'Return a book' button is disabled (as 'bob' has no books to return)
    /* 
      Relevant to checking borrow failed by being prevented to return
      books (since 'bob' has none)
    */
    cy.assert_return_disabled();

    // Logout
    cy.logout();

    /* ========= 'alice' returns 'The Great Gatsby' ========= */
    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Click 'Return a book' button
    cy.select_return();

    // Assert "The Great Gatsby" is displayed (as one of the books to return)
    /* 
      Relevant to verifying alice's earlier borrow of an 'Available'
      book was successful
    */
    cy.get("@the_great_gatsby").should("exist");

    // Click 'Return' button in 'The Great Gatsby'
    // Will be redirected to the menu
    cy.get("@the_great_gatsby").contains("Return").click();

    // Click 'Borrow a book' button
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

    // Click 'Borrow a book' button
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

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert '1984' displays its holder queue as 'Holders: [bob,alice]' (bob first, alice second)
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
    cy.login(ALICE, pass1);

    // Assert alice doesn't get a notification
    /*
      Relevant for showing that a holder that doesn't get notified
      (not first in queue) cannot borrow the book they're holding
    */
    cy.get("#notification-msg").should("have.text", "Notification: N/A");

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Attempt to borrow '1984'
    // Click 'Check Out' button
    cy.click_check_out_of("1984");
    // Click 'Yes' button to confirm borrow
    // Will get offered to place a hold since '1984' is unavailable
    cy.click_borrow_yes();
    // Click 'Yes' to confirm hold
    // Will get denied the hold (since 'alice' is already a holder)
    cy.click_hold_yes();

    // Assert the return button is disabled (since 'alice' shouldn't have succeeded in borrowing '1984')
    /* 
      Shows that a holder that doesn't get a notification cannot
      borrow the book
    */
    cy.assert_return_disabled();

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert 'alice' sees they are currently borrowing 0 books
    /* 
      Again, asserting that the borrow wasn't successful to show
      that an unnotfied holder cannot borrow the book
    */
    cy.assert_books_num(0);

    // Assert alice is stil in the hold queue (i.e. hold queue remained as [bob,alice])
    /*
      Again, asserting that the borrow wasn't successful
      since they're still holding the book
    */
    cy.assert_hold_queue("1984", "bob", "alice");

    // Go back to menu and logout
    cy.go("back");
    cy.logout();

    /* ========= 'bob' gets notification and manually borrows '1984' ======== */

    // Login as 'bob'
    cy.login(BOB, pass2);

    // Assert 'bob' sees notification that '1984' is available to borrow
    /* 
      Relevant to showing the notification is sent to the correct user 
      (the first in queue)
    */
    cy.get("#notification-msg").should(
      "have.text",
      "Notification: 1984 is now available!"
    );

    // Assert return button is disabled (because 'bob' shouldn't have automatically borrowed '1984' after it was returned)
    /*
      Relevant to queue advancement after a return, as
      bob should have to explcitily borrow the book
    */
    cy.assert_return_disabled();

    // Borrow '1984'
    cy.borrow_book("1984");

    /* ========= Assert 'bob' (notified) is the borrower of '1984' ========= */

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert bob sees they are currently borrowing 1 book
    /*
      Relevant to verifying that the borrow was successful
      for the notified holder
    */
    cy.assert_books_num(1);

    // Assert hold queue of '1984' is '[alice]' (i.e. alice is the only holder)
    /* 
      Relevant to verifying that the queue advances
      properly after bob (first in queue) borrowed the book
    */
    cy.assert_hold_queue("1984", "alice");

    // Go back to menu
    cy.go("back");

    // Click 'Return a book' button
    cy.select_return();

    // Assert '1984' appears in 'Return a book' page
    /*
      Relevant for verifying success of borrow
      for the notified holder (bob)
    */
    cy.get('[test-id="1984"]').should("exist");
  });

  it("borrowing_limit_and_hold_interactions", () => {
    /* ========= Setup: 'bob' borrows a book and 'alice' borrows 3 other books ======== */
    // Login as 'bob'
    cy.login(BOB, pass2);

    // Borrow 'To Kill a Mockingbird'
    cy.borrow_book("to-kill-a-mockingbird");

    // Logout
    cy.logout();

    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Borrow 3 books to reach borrow-limit
    cy.borrow_book("the-hobbit");
    cy.borrow_book("pride-and-prejudice");
    cy.borrow_book("the-catcher-in-the-rye");

    /* ========= 'alice' is able to place a hold on 'To Kill a Mockingbird' (despite being at the 3-book limit) ======== */
    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert 'alice' sees they have 3 books (limit)
    /* 
      Relevant to when it's later verified that
      'alice' doesn't exceed the limit when they
      attempt to borrow a 4th book
    */
    cy.assert_books_num(3);

    // Attempt to borrow a 4th book: 'To Kill a Mockingbird'
    // Click 'Check Out' button of 'To Kill a Mockingbird'
    cy.click_check_out_of("to-kill-a-mockingbird");
    // Click 'Yes' to confirm borrow
    // Since 'To Kill a Mockingbird' is unavailable, 'alice' is redirected to the hold confirmation page
    cy.click_borrow_yes();

    // Assert borrow limit message (in hold confirmation page)
    /*
      Relevant to verifying that the system prevents
      borrowing beyond the limit
    */
    cy.get("#place-hold-msg").should(
      "have.text",
      "You are at the 3-book limit, would you like to place a hold on To Kill a Mockingbird?"
    );

    // Click 'Yes' to confirm hold
    cy.click_hold_yes();

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert hold queue of '1984' is '[alice]' (i.e. alice is in the hold queue)
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

    /* ========= 'bob' returns 'To Kill a Mockingbird' ======== */
    // Login as 'bob'
    cy.login(BOB, pass2);

    // Return 'To Kill a Mockingbird'
    cy.return_book("to-kill-a-mockingbird");

    // Logout
    cy.logout();

    /* ========= 'alice' gets notified and attempts to borrows 'To Kill a Mockingbird' ======== */
    // Login as 'alice'
    cy.login(ALICE, pass1);

    // Assert notification for 'To Kill a Mockingbird'
    /*
      Relevant to verifying that a user at the
      3-book limit can still get notified about
      their hold
    */
    cy.get("#notification-msg").should(
      "have.text",
      "Notification: To Kill a Mockingbird is now available!"
    );

    // Attempt to borrow 'To Kill a Mockingbird'
    // Click 'Borrow a book' button
    cy.select_borrow();
    // Click 'Check Out' button of 'To Kill a Mockingbird'
    cy.click_check_out_of("to-kill-a-mockingbird");
    // Click 'Yes' to confirm the borrow
    cy.click_borrow_yes();
    // Get denied the borrow, click yes to confirm hold
    // Will get denied the hold (since 'alice' is already a holder) and redirected to the menu
    cy.click_hold_yes(); // Will offer a hold, but will get denied since alice is already a holder

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert that 'alice' sees they are currently borrowing 3 books
    /*
      Relevant to verifying 3-book limit is enforced, even
      if she was notified
    */
    cy.assert_books_num(3);

    // Go back to menu
    cy.go("back");

    /* ========= 'alice' gains borrowing capacity by returning a book ======== */
    // Click 'Return a book' button
    cy.select_return();

    // Assert 'To Kill a Mockingbird' is not in return list
    /*
      Relevant to verifying 3-book limit is enforced, even
      if she was notified. She must gain borrowing capacity
      to be able to borrow.
    */
    cy.get('[test-id="to-kill-a-mockingbird"]').should("not.exist");

    // Click 'Return' button of 'The Hobbit'
    cy.get('[test-id="the-hobbit"]').contains("Return").click();

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert 'alice' is currently borrowing 2 books (gained borrowing capacity)
    /*
      Relevant to verifying gained capacity, she should be able
      to borrow a book because she has < 3 books
    */
    cy.assert_books_num(2);

    // Attempt to borrow 'To Kill a Mockingbird'
    // Click 'Check Out' button for 'To Kill a Mockingbird'
    cy.click_check_out_of("to-kill-a-mockingbird");
    // Click 'Yes' button to confirm borrowing
    cy.click_borrow_yes();

    // Select 'Return a book' button
    cy.select_return();

    // Assert 'To Kill a Mockingbird' appears in 'alice's return page
    /*
      Relevant to verifying gained capacity after returning
      a book, as she succeded to borrowing 'To Kill a Mockingbird'
    */
    cy.get('[test-id="to-kill-a-mockingbird"]').should("exist");

    // Go back to menu
    cy.go("back");

    // Click 'Borrow a book' button
    cy.select_borrow();

    // Assert 'alice' sees they are currently borrowing 3 books
    /*
      Relevant to verifying that alice was able to borrow 
      'To Kill a Mockingbird' after gaining capacity
    */
    cy.assert_books_num(3);
  });
});
