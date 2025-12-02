import { pageTemplate } from "./pageTemplate.js";

export function holdConfirmationPage(bookIndex) {
  const title = "Place a hold";
  const contents = `
  <div class="confirmation-container">
      <p id="place-hold-msg"></p>

      <div class="yes-no-btns">
        <button class="btn-regular" id="no-btn" onclick="redirectMenu()">No</button>
        <button class="btn-regular" test-id="hold-yes-btn" id="yes-btn" onclick="handleAcceptHold(${bookIndex})" disabled>Yes</button>
       </div>
    </div>
    `;
  return pageTemplate(title, contents, `loadHoldConfirmation(${bookIndex})`);
}
