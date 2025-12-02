import { pageTemplate } from "./pageTemplate.js";

export function selectBorrowBookPage() {
  const title = "Borrow a book";
  const contents = `
    <p id="borrowed-books-num"></p>
    <div id="borrow-books-display"></div>
  `;
  return pageTemplate(title, contents, "loadBorrowABook()");
}
