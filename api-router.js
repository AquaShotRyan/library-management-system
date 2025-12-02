import express from "express";
import LibraryDate from "./main/LibraryDate.js";

const router = express.Router();

let library;
const TODAY = new Date(2025, 10, 25); // FAKE TODAY FOR TESTING

router.use((req, res, next) => {
  library = req.library;
  next();
});

// Authentication
router.post("/login", (req, res) => {
  try {
    const { username, password } = req.body;
    const result = library.login(username, password);
    res.status(200);
    res.json({ result });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.post("/logout", (req, res) => {
  try {
    library.logout();
    res.status(200);
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/sessionUsername", (req, res) => {
  res.status(200);
  res.json({ username: library.getSessionUsername() });
});

// Books
router.get("/books", (req, res) => {
  try {
    const books = library.getAllBooks();
    res.status(200);
    res.json(books.map((b) => serializeBook(b, library.getSessionUsername())));
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

// Checkout
router.post("/checkout/:bookIndex", (req, res) => {
  try {
    const bookTitle = getBookByIndex(req.params.bookIndex).getTitle();
    library.checkoutBook(
      bookTitle,
      library.getSessionUsername(),
      new LibraryDate(TODAY)
    );
    res.status(200);
    res.json({ ok: true });
  } catch (e) {
    res.status(400).json({ error: String(e) });
  }
});

router.get("/verifyBorrowing/:bookIndex", (req, res) => {
  try {
    const book = getBookByIndex(req.params.bookIndex);
    const bookTitle = book.getTitle();
    const result = library.verifyBorrowing(
      bookTitle,
      library.getSessionUsername()
    );
    res.status(200);
    res.json({ result });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

// Holds
router.post("/placehold/:bookIndex", (req, res) => {
  try {
    const book = getBookByIndex(req.params.bookIndex);
    const bookTitle = book.getTitle();
    library.placeHold(bookTitle, library.getSessionUsername());
    res.status(200);
    res.json({ ok: true });
  } catch (e) {
    res.status(400).json({ error: String(e) });
  }
});

router.get("/verifyHolding/:bookIndex", (req, res) => {
  try {
    const book = getBookByIndex(req.params.bookIndex);
    const bookTitle = book.getTitle();
    const result = library.verifyHolding(
      bookTitle,
      library.getSessionUsername()
    );
    res.status(200);
    res.json({ result });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

// Return
router.post("/return/:userBookIndex", (req, res) => {
  try {
    const book = library.getBorrowedBooksSorted(library.getSessionUsername())[
      req.params.userBookIndex
    ];
    library.returnBook(book.getTitle(), library.getSessionUsername());
    res.status(200);
    res.json({ ok: true });
  } catch (e) {
    res.status(400).json({ error: String(e) });
  }
});

// Borrowed books
router.get("/borrowed", (req, res) => {
  try {
    const list = library.getBorrowedBooksSorted(library.getSessionUsername());
    res.status(200);
    res.json(
      list.map((b) => ({
        title: b.getTitle(),
        author: b.getAuthor(),
        dueDate: b.getDueDateStr(),
      }))
    );
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/borrowers/size", (req, res) => {
  res.status(200);
  res.json({ size: library.getBorrowersSize() });
});

// Utility/boolean endpoints
router.get("/isAtBorrowingCapacity", (req, res) => {
  try {
    res.status(200);
    res.json({
      isAtBorrowingCapacity: library.isAtBorrowingCapacity(
        library.getSessionUsername()
      ),
    });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/heldBookIsAvailable", (req, res) => {
  try {
    const isAvailable = library.heldBookIsAvailable(
      library.getSessionUsername()
    );
    res.status(200);
    res.json({ isAvailable });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/heldBook", (req, res) => {
  try {
    const username = library.getSessionUsername();
    if (!username) return res.status(401).json({ error: "not logged in" });

    const book = library.getHeldBook(username);
    if (!book) return res.status(404).json({ error: "no held book" });

    res.status(200);
    res.json(serializeBook(book, username));
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/borrowed/num", (req, res) => {
  try {
    const num = library.getBorrowedBooksNum(library.getSessionUsername());
    res.status(200);
    res.json({ num });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/potentialDueDate", (req, res) => {
  try {
    const potentialDueDate = library.getPotentialDueDateStr(
      new LibraryDate(TODAY)
    );
    res.status(200);
    res.json({ potentialDueDate });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

router.get("/canReturnBooks", (req, res) => {
  try {
    const canReturnBooks = library.canReturnBooks(library.getSessionUsername());
    res.status(200);
    res.json({ canReturnBooks });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

export default router;

/* ============= Helpers ============= */
function serializeBook(book, username) {
  if (!book) return null;
  const curHolder = book.getCurHolder();
  const curBorrower = book.getCurBorrower();
  return {
    title: book.getTitle(),
    author: book.getAuthor(),
    dueDate: book.getDueDateStr(),
    holdersNum: book.getHoldersNum(),
    curHolder:
      curHolder && typeof curHolder.getUsername === "function"
        ? curHolder.getUsername()
        : null,
    curBorrower:
      curBorrower && typeof curBorrower.getUsername === "function"
        ? curBorrower.getUsername()
        : null,
    availability: username ? book.getAvailabilityStatus(username) : null,
    holdQueue: book.getQueue().map((user) => user.getUsername()),
  };
}

function getBookByIndex(bookIndex) {
  return library.getAllBooks()[bookIndex];
}
