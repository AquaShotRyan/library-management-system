import { User } from "./User.js";
import BorrowedBooks from "./BorrowedBooks.js";
import BookDetails from "./BookDetails.js";

export default class Borrower extends User {
  constructor(username, password) {
    super(username, password);
    this.borrowedBooks = new BorrowedBooks();
    this.curHold = null;
  }

  getBorrowedBooks() {
    return this.borrowedBooks;
  }

  getBorrowedBooksList() {
    return this.borrowedBooks.getBorrowedBooksList();
  }

  hasBook(bookTitle) {
    return this.borrowedBooks.getBookByTitle(bookTitle) !== null;
  }

  hasHold() {
    return this.curHold != null;
  }

  curHoldIs(bookTitle) {
    if (!this.hasHold()) return false;
    return this.curHold.getTitle() === bookTitle;
  }

  addBorrowedBook(b) {
    this.borrowedBooks.addBook(b);
  }

  removeBorrowedBook(bookTitle) {
    this.borrowedBooks.removeBook(bookTitle);
  }

  getBorrowedBooksNum() {
    return this.borrowedBooks.size();
  }

  setCurHold(book) {
    this.curHold = book;
  }

  getCurHold() {
    return this.curHold;
  }
}
