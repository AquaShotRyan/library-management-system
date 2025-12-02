import InitializeLibrary from "./InitializeLibrary.js";
import LibraryAuth from "./LibraryAuth.js";
import BorrowTransaction from "./BorrowTransaction.js";
import { AvailabilityEnum } from "./AvailabilityEnum.js";

export default class Library {
  static BORROWING_DAY_LENGTH = 14;
  static BORROWING_LIMIT = 3;

  constructor() {
    const initLibrary = new InitializeLibrary();
    this.catalogue = initLibrary.initCatalogue();
    this.borrowers = initLibrary.initBorrowers();
    this.borrowTransactions = [];

    this.auth = new LibraryAuth(this.borrowers);
    this.sessionBorrower = null;
  }

  /* ============ Main Methods ============ */

  /* ------- authentication ------- */
  login(username, password) {
    const authResult = this.auth.authUser(username, password);

    // AuthEnum values are strings — set sessionBorrower when success
    if (authResult === "SUCCESS") {
      this.sessionBorrower = this.borrowers.getBorrower(username);
    }

    return authResult;
  }

  logout() {
    this.sessionBorrower = null;
  }

  getSessionUsername() {
    if (this.sessionBorrower == null) return null;
    return this.sessionBorrower.getUsername();
  }

  /* ------- borrowing ------- */
  checkoutBook(bookTitle, username, today) {
    this.setBorrower(bookTitle, username);
    this.setDueDateFromDate(bookTitle, today);
    this.addBorrowTransaction(
      new BorrowTransaction(bookTitle, username, today.toString())
    );
  }

  setBorrower(bookTitle, username) {
    const book = this.getBook(bookTitle);
    const borrower = this.borrowers.getBorrower(username);

    // set user as borrower
    book.setCurBorrower(borrower);

    // remove borrower as current holder if they're the current holder
    if (book.curHolderIs(username)) {
      book.popHolder();
    }

    // add book to Borrower's checked-out books
    this.addBookToBorrower(book, username);
  }

  addBookToBorrower(book, username) {
    const borrower = this.borrowers.getBorrower(username);
    if (borrower.hasBook(book.getTitle()))
      throw new Error(`${username} already has ${book.getTitle()} checked out`);
    borrower.addBorrowedBook(book);
  }

  setDueDateFromDate(bookTitle, date) {
    date.addDays(Library.BORROWING_DAY_LENGTH);

    const book = this.getBook(bookTitle);
    book.setDueDate(date);
  }

  verifyBorrowing(bookTitle, username) {
    const book = this.getBook(bookTitle);

    let result = "CAN_BORROW";

    if (this.isAtBorrowingCapacity(username)) {
      result = "AT_BORROWING_LIMIT";
    } else if (book.hasBorrower()) {
      const isCurUser = book.curBorrowerIs(username);
      result = isCurUser ? "CHECKED_OUT_BY_USER" : "CHECKED_OUT_BY_ANOTHER";
    } else if (book.hasHolder()) {
      const isCurUser = book.curHolderIs(username);
      result = isCurUser ? result : "ON_HOLD_BY_ANOTHER";
    }
    return result;
  }

  /* ------- holding ------- */
  placeHold(bookTitle, username) {
    const book = this.getBook(bookTitle);
    const borrower = this.borrowers.getBorrower(username);

    book.addHolder(borrower);
    borrower.setCurHold(book.getBookDetails());
  }

  addToHoldQueue(bookTitle, username) {
    const book = this.getBook(bookTitle);
    const user = this.borrowers.getBorrower(username);
    if (book.containsHolder(user)) {
      throw new Error(
        `${username} already has a hold or is in the queue of ${bookTitle}`
      );
    }
    book.addHolder(user);
  }

  verifyHolding(bookTitle, username) {
    const book = this.getBook(bookTitle);
    const borrower = this.borrowers.getBorrower(username);

    if (borrower.hasHold() && !borrower.curHoldIs(bookTitle)) {
      return "AT_HOLD_LIMIT";
    }
    if (book.curHolderIs(username) || book.containsHolder(borrower)) {
      return "ON_HOLD_BY_USER";
    }
    if (!book.hasBorrower() && !book.hasHolder()) {
      return "BOOK_IS_AVAILABLE";
    }
    return "CAN_HOLD";
  }

  /* ------- returning ------- */
  returnBook(bookTitle, username) {
    const book = this.getBook(bookTitle);

    this.removeBorrowerFromBook(bookTitle, username);
    this.removeBookFromBorrower(bookTitle, username);
    book.setDueDate(null);
  }

  removeBorrowerFromBook(bookTitle, username) {
    const book = this.getBook(bookTitle);
    if (!book.hasBorrower()) throw new Error("No borrower to remove");
    if (!book.curBorrowerIs(username))
      throw new Error(`Username '${username}' doesn't match current borrower`);

    book.removeCurBorrower();
  }

  removeBookFromBorrower(bookTitle, username) {
    const borrower = this.borrowers.getBorrower(username);

    if (!borrower.hasBook(bookTitle))
      throw new Error(`Book '${bookTitle}' is not checked out by ${username}`);

    borrower.removeBorrowedBook(bookTitle);
  }

  /* ============  Logging Transactions ============  */
  addBorrowTransaction(b) {
    this.borrowTransactions.push(b);
  }

  getBorrowTransaction(index) {
    return this.borrowTransactions[index] || null;
  }

  getBorrowerTransactionsSize() {
    return this.borrowTransactions.length;
  }

  /* ============  Getters / Setters ============  */
  getBook(title) {
    return this.catalogue.getBook(title);
  }

  getAllBooks() {
    const books = this.catalogue.getAllBooks();
    books.sort((a, b) => a.getAuthor().localeCompare(b.getAuthor()));
    return books;
  }

  getBorrowedBooks(username) {
    const borrower = this.borrowers.getBorrower(username);
    return borrower.getBorrowedBooks();
  }

  getBorrowedBooksNum(username) {
    const borrower = this.borrowers.getBorrower(username);
    return borrower.getBorrowedBooksNum();
  }

  getBorrowedBooksSorted(username) {
    const borrower = this.borrowers.getBorrower(username);
    const borrowedBooksList = borrower.getBorrowedBooksList();
    borrowedBooksList.sort((a, b) =>
      a.getAuthor().localeCompare(b.getAuthor())
    );
    return borrowedBooksList;
  }

  getHeldBook(username) {
    const borrower = this.borrowers.getBorrower(username);
    return this.getBook(borrower.getCurHold().getTitle());
  }

  getBorrowersSize() {
    return this.borrowers.getBorrowersSize();
  }

  getAllBooksWithAvailability(username, availability) {
    const allBooks = this.getAllBooks();
    const filteredBooks = allBooks.filter(
      (book) => book.getAvailabilityStatus(username) === availability
    );
    return filteredBooks;
  }

  getPotentialDueDateStr(curDate) {
    curDate.addDays(Library.BORROWING_DAY_LENGTH);
    return curDate.toString();
  }

  /* ============ Booleans ============ */
  borrowerHasBook(bookTitle, username) {
    return this.borrowers.getBorrower(username).hasBook(bookTitle);
  }

  borrowerIsHoldingBook(bookTitle, username) {
    return this.borrowers.getBorrower(username).curHoldIs(bookTitle);
  }

  heldBookIsAvailable(username) {
    const borrower = this.borrowers.getBorrower(username);
    if (!borrower.hasHold()) return false;
    const heldBook = this.getBook(borrower.getCurHold().getTitle());
    return (
      heldBook.getAvailabilityStatus(username) === AvailabilityEnum.AVAILABLE
    );
  }

  canReturnBooks(username) {
    return this.getBorrowedBooksNum(username) > 0;
  }

  userIsInHoldQueue(bookTitle, username) {
    const book = this.getBook(bookTitle);
    const user = this.borrowers.getBorrower(username);
    return book.containsHolder(user);
  }

  isAtBorrowingCapacity(username) {
    return this.getBorrowedBooksNum(username) >= Library.BORROWING_LIMIT;
  }
}
