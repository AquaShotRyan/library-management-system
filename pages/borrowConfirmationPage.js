import { pageTemplate } from "./pageTemplate.js";

export function borrowConfirmationPage(bookIndex) {
  const title = "Borrow a book";
  const contents = `
      <div class="confirmation-container">
        <p id="confirmation-book-details-msg"></p>
        <p id="confirmation-due-date-msg"></p>
        <div class="yes-no-btns">
          <button class="btn-regular" id="no-btn" onclick="redirectMenu()">No</button>
          <button class="btn-regular" test-id="borrow-yes-btn" id="yes-btn" onclick="handleAcceptBorrow(${bookIndex})" disabled>Yes</button>
        </div>
      </div>
    `;
  return pageTemplate(title, contents, `loadBorrowConfirmation(${bookIndex})`);
}
