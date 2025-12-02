import Catalogue from "./Catalogue.js";
import Borrowers from "./Borrowers.js";
import Book from "./Book.js";
import Borrower from "./Borrower.js";
import bookData from "./BookData.json" with { type: 'json' };
import users from "./UserData.json" with { type: 'json' };

export default class InitializeLibrary {
  constructor() {
    this.catalogue = new Catalogue();
    this.borrowers = new Borrowers();
  }

  initCatalogue() {
    for (const book of bookData) {
      this.catalogue.addBook(new Book(book.title, book.author));
    }
    return this.catalogue;
  }

  initBorrowers() {
    for (const u of users) {
      this.borrowers.addBorrower(new Borrower(u.username, u.password));
    }
    return this.borrowers;
  }
}
