export default class Catalogue {
  constructor() {
    this.catalogue = new Map();
  }

  addBook(book) {
    const title =
      typeof book.getTitle === "function" ? book.getTitle() : book.title;
    this.catalogue.set(title, book);
  }

  getBook(title) {
    return this.catalogue.get(title) || null;
  }

  getCatalogueSize() {
    return this.catalogue.size;
  }

  getAllBooks() {
    return Array.from(this.catalogue.values());
  }
}
