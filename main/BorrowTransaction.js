export default class BorrowTransaction {
  constructor(title, borrowerName, date) {
    this.bookTitle = title;
    this.borrower = borrowerName;
    this.borrowDate = date;
  }

  getBookTitle() {
    return this.bookTitle;
  }

  getBorrower() {
    return this.borrower;
  }

  getBorrowDate() {
    return this.borrowDate;
  }
}
