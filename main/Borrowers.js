export default class Borrowers {
  constructor() {
    this.borrowers = new Map();
  }

  addBorrower(b) {
    const username =
      typeof b.getUsername === "function" ? b.getUsername() : b.username;
    this.borrowers.set(username, b);
  }

  getBorrower(username) {
    return this.borrowers.get(username) || null;
  }

  getBorrowersSize() {
    return this.borrowers.size;
  }
}
