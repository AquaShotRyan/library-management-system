export default class BorrowedBooks {
  constructor() {
    this.books = [];
  }

  getBookByTitle(bookTitle) {
    for (let i = 0; i < this.size(); ++i) {
      const b = this.books[i];
      const title = typeof b.getTitle === "function" ? b.getTitle() : b.title;
      if (title === bookTitle) return b;
    }
    return null;
  }

  addBook(b) {
    this.books.push(b);
  }

  removeBook(bookTitle) {
    for (let i = 0; i < this.size(); ++i) {
      const b = this.books[i];
      const title = typeof b.getTitle === "function" ? b.getTitle() : b.title;
      if (title === bookTitle) {
        this.books.splice(i, 1);
        return;
      }
    }
  }

  getBorrowedBooksList() {
    return this.books;
  }

  size() {
    return this.books.length;
  }
}
