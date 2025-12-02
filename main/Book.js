import BookDetails from "./BookDetails.js";
import { AvailabilityEnum } from "./AvailabilityEnum.js";

export default class Book {
  constructor(title, author) {
    this.bookDetails = new BookDetails(title, author);
    this.dueDate = null;
    this.holdQueue = [];
    this.curBorrower = null;
  }

  addHolder(user) {
    this.holdQueue.push(user);
  }

  popHolder() {
    return this.holdQueue.shift() || null;
  }

  removeCurBorrower() {
    this.curBorrower = null;
    return null;
  }

  getAvailabilityStatus(username) {
    if (this.hasHolder() && !this.hasBorrower() && this.curHolderIs(username))
      return AvailabilityEnum.AVAILABLE;
    if (this.hasBorrower()) return AvailabilityEnum.CHECKED_OUT;
    if (this.hasHolder()) return AvailabilityEnum.ON_HOLD;
    return AvailabilityEnum.AVAILABLE;
  }

  // getters
  getTitle() {
    return this.bookDetails.getTitle();
  }
  getAuthor() {
    return this.bookDetails.getAuthor();
  }
  getDueDate() {
    return this.dueDate === null ? null : this.dueDate;
  }
  getHoldersNum() {
    return this.holdQueue.length;
  }
  getCurHolder() {
    return this.holdQueue.length ? this.holdQueue[0] : null;
  }
  getBookDetails() {
    return this.bookDetails;
  }
  getCurBorrower() {
    return this.curBorrower;
  }
  getDueDateStr() {
    return this.dueDate == null ? "N/A" : String(this.dueDate);
  }
  getQueue() {
    return this.holdQueue;
  }

  // setters
  setDueDate(date) {
    this.dueDate = date;
  }
  setCurBorrower(borrower) {
    this.curBorrower = borrower;
  }

  // booleans
  containsHolder(user) {
    return this.holdQueue.some((u) =>
      typeof u.equals === "function" ? u.equals(user) : u === user
    );
  }
  hasBorrower() {
    return this.curBorrower != null;
  }
  hasHolder() {
    return this.holdQueue.length > 0;
  }
  curBorrowerIs(username) {
    if (!this.hasBorrower()) return false;
    return username === this.curBorrower.getUsername();
  }
  curHolderIs(username) {
    if (!this.hasHolder()) return false;
    const h = this.getCurHolder();
    return (
      h && typeof h.getUsername === "function" && username === h.getUsername()
    );
  }

  toString() {
    return `${this.getTitle()} by ${this.getAuthor()}`;
  }
}
