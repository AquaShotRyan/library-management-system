const URL = "http://localhost:3000";

Cypress.Commands.add("login", (username, password) => {
  // Type in username
  cy.get("#input-username").type(username);

  // Type in username
  cy.get("#input-password").type(password);

  // Click 'Login' button
  cy.get("#btn-login").click();
});

Cypress.Commands.add("select_borrow", () => {
  // Click 'Borrow a book' button (from menu)
  cy.get('[test-id="borrow-btn"]').click();
});

Cypress.Commands.add("select_return", () => {
  // Click 'Return a book' button (from menu)
  cy.get('[test-id="return-btn"]').click();
});

Cypress.Commands.add("logout", () => {
  // Click 'Logout' button (from menu)
  cy.get('[test-id="logout-btn"]').click();
});

Cypress.Commands.add("assert_availability", (availability) => {
  // Assert displayed availability status ("Available" | "Checked Out" | "On Hold")
  cy.get('[test-id="availability-status"]').should("have.text", availability);
});

Cypress.Commands.add("assert_books_num", (numBooks) => {
  // Assert user sees "You are currently borrowing [numBooks] books" (in 'Borrow a book' page)
  cy.get("#borrowed-books-num").should(
    "have.text",
    `You are currently borrowing ${numBooks} books`
  );
});

Cypress.Commands.add("click_borrow_yes", () => {
  // Click the 'Yes' button in the borrow confirmation page
  cy.get('[test-id="borrow-yes-btn"]').click();
});

Cypress.Commands.add("click_hold_yes", () => {
  // Click the 'Yes' button in the hold confirmation page
  cy.get('[test-id="hold-yes-btn"]').click();
});

Cypress.Commands.add("click_no", () => {
  // Click the 'No' button (in either borrow or hold confirmation page)
  cy.get("#no-btn").click();
});

Cypress.Commands.add("click_check_out_of", (bookTestId) => {
  // Click the 'Check Out' button inside a book (when on 'Borrow a book' page)
  cy.get(`[test-id="${bookTestId}"]`).contains("Check Out").click();
});

Cypress.Commands.add("assert_return_disabled", () => {
  // Click the 'Return' button inside of a book (when on the 'Return a book' page)
  cy.get('[test-id="return-btn"]').should("be.disabled");
});

Cypress.Commands.add("borrow_book", (bookTestId) => {
  // Note: this command only works if the user is in the menu, the book is available, and the user has borrowing capacity

  // Select 'Borrow a book' button
  cy.select_borrow();

  // Click the 'Check Out' button of [bookTestId]
  cy.click_check_out_of(bookTestId);

  // Click 'Yes' when asked to confirm the borrow
  cy.click_borrow_yes();
});

Cypress.Commands.add("hold_book", (bookTestId) => {
  // Note: this command only works if the user is in the menu, the book is checked out, and the user isn't holding a book

  // User attempts to borrow a book, but is offered to place a hold
  cy.borrow_book(bookTestId);

  // Click 'Yes' button to accept hold
  cy.click_hold_yes();
});

Cypress.Commands.add("return_book", (bookTestId) => {
  // Click 'Return a book' (in menu)
  cy.select_return();

  // Click the 'Return' button of [bookTestId] to return the book
  cy.get(`[test-id="${bookTestId}"]`).contains("Return").click();
});

Cypress.Commands.add("assert_hold_queue", (bookTestId, ...expectedHolders) => {
  let holdersListStr = `[${expectedHolders.join(",")}]`;
  cy.get(`[test-id="${bookTestId}"]`).within(() => {
    // Assert if the hold queue of [bookTestId] matches [expectedHolders]
    cy.get('[test-id="hold-queue"]').should(
      "have.text",
      `Holders: ${holdersListStr}`
    );
  });
});
