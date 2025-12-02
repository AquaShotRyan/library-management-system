const URL = "http://localhost:3000";

Cypress.Commands.add("login", (username, password) => {
  cy.get("#input-username").type(username);
  cy.get("#input-password").type(password);
  cy.get("#btn-login").click();
});

Cypress.Commands.add("select_borrow", () => {
  cy.get('[test-id="borrow-btn"]').click();
});

Cypress.Commands.add("select_return", () => {
  cy.get('[test-id="return-btn"]').click();
});

Cypress.Commands.add("logout", () => {
  cy.get('[test-id="logout-btn"]').click();
});

Cypress.Commands.add("assert_availability", (availability) => {
  cy.get('[test-id="availability-status"]').should("have.text", availability);
});

Cypress.Commands.add("assert_books_num", (numBooks) => {
  cy.get("#borrowed-books-num").should(
    "have.text",
    `You are currently borrowing ${numBooks} books`
  );
});

Cypress.Commands.add("click_borrow_yes", () => {
  cy.get('[test-id="borrow-yes-btn"]').click();
});

Cypress.Commands.add("click_hold_yes", () => {
  cy.get('[test-id="hold-yes-btn"]').click();
});

Cypress.Commands.add("click_no", () => {
  cy.get("#no-btn").click();
});

Cypress.Commands.add("click_check_out_of", (bookTestId) => {
  cy.get(`[test-id="${bookTestId}"]`).contains("Check Out").click();
});

Cypress.Commands.add("assert_return_disabled", () => {
  cy.get('[test-id="return-btn"]').should("be.disabled");
});

Cypress.Commands.add("borrow_book", (bookTestId) => {
  cy.select_borrow();
  cy.click_check_out_of(bookTestId);
  cy.click_borrow_yes() // Confirm borrow
});

Cypress.Commands.add("hold_book", (bookTestId) =>  {
  cy.borrow_book(bookTestId);
  cy.click_hold_yes(); // Redirected to hold confirmation page
});

Cypress.Commands.add("return_book", (bookTestId) => {
  cy.select_return();
  cy.get(`[test-id="${bookTestId}"]`).contains("Return").click();
});

Cypress.Commands.add("assert_hold_queue", (bookTestId, ...expectedHolders) => {
  let holdersListStr = `[${expectedHolders.join(",")}]`;
  cy.get(`[test-id="${bookTestId}"]`).within(() => {
    cy.get('[test-id="hold-queue"]').should("have.text", `Holders: ${holdersListStr}`);
  }); 
}); 
   
