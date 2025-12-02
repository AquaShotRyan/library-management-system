import express from "express";
import { fileURLToPath } from "url";
import { dirname } from "path";
import apiRouter from "./api-router.js";

// import pages
import { loginPage } from "./pages/loginPage.js";
import { menuPage } from "./pages/menuPage.js";
import { selectBorrowBookPage } from "./pages/selectBorrowBookPage.js";
import { borrowConfirmationPage } from "./pages/borrowConfirmationPage.js";
import { holdConfirmationPage } from "./pages/holdConfirmationPage.js";
import { selectReturnBookPage } from "./pages/selectReturnBookPage.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const app = express();
const PORT = 3000;

const { default: Library } = await import("./main/Library.js");

// Stores
let library = new Library(); // Library instance

// Middleware
app.use(express.json());
app.use(express.static(__dirname + "/public"));

app.use((req, res, next) => {
  console.log(
    `Req from ${library.getSessionUsername()}: ${req.method} ${req.path}`
  );
  next();
});
app.use((req, res, next) => {
  req.library = library;
  next();
});

/* ============= Library API ============= */

// Only for testing
app.delete("/api/reset", (req, res) => {
  try {
    library = new Library();
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

// Library API routes
app.use("/api", apiRouter);

/* ============= Pages ============= */

app.get("/", (req, res) => {
  res.redirect("/login");
});

app.get("/login", (req, res) => {
  res.set("Content-Type", "text/html");
  res.send(`` + loginPage());
});

app.get("/menu", (req, res) => {
  res.set("Content-Type", "text/html");
  res.send(`` + menuPage());
});

app.get("/borrow", (req, res) => {
  res.set("Content-Type", "text/html");
  res.send(`` + selectBorrowBookPage());
});

app.get("/borrow/confirm/:bookIndex", (req, res) => {
  res.set("Content-Type", "text/html");
  res.send(`` + borrowConfirmationPage(req.params.bookIndex));
});

app.get("/placehold/:bookIndex", (req, res) => {
  res.set("Content-Type", "text/html");
  res.send(`` + holdConfirmationPage(req.params.bookIndex));
});

app.get("/return", (req, res) => {
  res.set("Content-Type", "text/html");
  res.send(`` + selectReturnBookPage());
});

app.listen(PORT, () => console.log(`Library API listening on port ${PORT}`));
