import { pageTemplate } from "./pageTemplate.js";

export function selectReturnBookPage() {
  const title = "Return a book";
  const contents = `
    <div id="return-books-display"></div>
  `;
  return pageTemplate(title, contents, "loadReturnABook()");
}
