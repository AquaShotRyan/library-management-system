import { pageTemplate } from "./pageTemplate.js";

export function menuPage() {
  const title = "Menu";
  const contents = `
    <p id="welcome-msg"></p>
    <p id="notification-msg"></p>
    <div id="menu-btns">
      <button class="btn-big" onclick="handleBorrowBtn()" id="borrow-btn" test-id="borrow-btn" disabled>Borrow a book</button>
      <button class="btn-big" onclick="handleReturnBtn()" id="return-btn" test-id="return-btn" disabled>Return a book</button>
      <button class="btn-big" onclick="handleLogoutBtn()" id="logout-btn" test-id="logout-btn" disabled>Logout</button>
    </div>
  `;
  return pageTemplate(title, contents, "loadMenu()");
}
