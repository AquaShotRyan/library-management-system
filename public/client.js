/* ============ Button Functions ============ */
async function handleLoginBtn() {
  // get strings from text inputs
  const username = document.getElementById("input-username").value;
  const password = document.getElementById("input-password").value;

  const responseJson = await postApiJson("/api/login", {
    username: username,
    password: password,
  });

  if (responseJson.result == "SUCCESS") {
    redirectMenu();
  } else {
    document.getElementById("login-err").innerHTML = "Invalid credentials";
  }
}

async function handleBorrowBtn() {
  window.location.href = "/borrow";
}

async function handleReturnBtn() {
  window.location.href = "/return";
}

async function handleLogoutBtn() {
  const wantsToLogout = confirm("Are you sure you want to logout?");
  if (wantsToLogout) {
    await fetch("/api/logout", {
      method: "POST",
    });
    window.location.href = "/login";
  }
}

async function redirectMenu() {
  window.location.href = "/menu";
}

async function handleCheckOutBtn(index) {
  window.location.href = "/borrow/confirm/" + index;
}

async function handleAcceptBorrow(index) {
  const book = await getBookByIndex(index);

  // Verify user can borrow
  const borrowResJson = await getApiJson("/api/verifyBorrowing/" + index);

  // Checkout and show confirmation
  if (borrowResJson.result == "CAN_BORROW") {
    // Checkout book
    const dueDateJson = await getApiJson("/api/potentialDueDate");
    const response = await fetch("/api/checkout/" + index, {
      method: "POST",
    });

    if (response.ok) {
      alert(
        `${book.title} by ${book.author} has been borrowed and is due on ${dueDateJson.potentialDueDate}`
      );
      redirectMenu();
    }
  } else if (borrowResJson.result == "CHECKED_OUT_BY_USER") {
    alert("You already have this book checked out");
    redirectMenu();
  } else {
    window.location.href = "/placehold/" + index;
  }
}

async function handleAcceptHold(index) {
  const book = await getBookByIndex(index);

  // Verify user can hold
  const holdResJson = await getApiJson("/api/verifyHolding/" + index);

  // Place hold
  if (holdResJson.result == "CAN_HOLD") {
    await fetch("/api/placehold/" + index, {
      method: "POST",
    });

    alert(`You have been placed in the hold queue of ${book.title}`);
  } else if (holdResJson.result == "AT_HOLD_LIMIT") {
    alert("You already are already holding another book");
  } else if (holdResJson.result == "BOOK_IS_AVAILABLE") {
    alert("You cannot hold this book because it's available");
  } else if (holdResJson.result == "ON_HOLD_BY_USER") {
    alert("You already are already holding this book");
  }
  redirectMenu();
}

async function handleReturnBookBtn(bookIndex) {
  const book = await getReturnBookByIndex(bookIndex);

  const res = await fetch("/api/return/" + bookIndex, { method: "POST" });
  const resJson = await res.json();
  if (resJson.ok) {
    alert(`${book.title} has been returned`);
    redirectMenu();
  }
}

async function handleResetBtn() {
  const wantsToReset = confirm("Are you sure you want to reset the library?");

  if (!wantsToReset) return;

  const response = await fetch("/test/reset", {
    method: "DELETE",
  });
  if (response.ok) {
    window.location.href = "/login";
  }
}

/* ============ Onload Functions ============ */
async function loadMenu() {
  const borrowBtn = document.getElementById("borrow-btn");
  const returnBtn = document.getElementById("return-btn");
  const logoutBtn = document.getElementById("logout-btn");
  const welcomeMsg = document.getElementById("welcome-msg");
  const notifMsg = document.getElementById("notification-msg");

  // Display logged in user
  const username = await getSessionUsername();
  welcomeMsg.innerHTML = `Hello <span style="color:#75B4FF"><b>${username}</b></span>`;

  // Display notification if held book is available
  const notifJson = await getApiJson("/api/heldBookIsAvailable");
  notifMsg.innerHTML = "Notification: N/A";
  if (notifJson == null) {
    console.error("Failed to fetch /api/heldBookIsAvailable");
  }else if (notifJson.isAvailable) {
    const heldBookJson = await getApiJson("/api/heldBook");

    notifMsg.innerHTML = `Notification: <b>${heldBookJson.title}</b> is now available!`;
  }

  // Enable Return button if user has books to return
  const canReturnJson = await getApiJson("/api/canReturnBooks");
  if (canReturnJson.canReturnBooks) {
    returnBtn.disabled = false;
  }

  // Enable buttons after requests are finished
  borrowBtn.disabled = false;
  logoutBtn.disabled = false;
}

async function loadBorrowABook() {
  // Display current number of borrowed books
  const numBooksJson = await getApiJson("/api/borrowed/num");
  document.getElementById(
    "borrowed-books-num"
  ).innerHTML = `You are currently borrowing <b>${numBooksJson.num}</b> books`;

  // Display books grid
  let contents = "";
  const allBooks = await getApiJson("/api/books");
  const username = await getSessionUsername();

  for (let i = 0; i < allBooks.length; ++i) {
    let book = allBooks[i];
    const dueDate = book.curBorrower === username ? book.dueDate : "N/A";
    const availabilityColors = getAvailabilityStatusColor(book.availability);
    const noHolders = book.holdQueue.length == 0;
    contents += `
      <div class="book-card" test-id="${slugify(book.title)}">
        <b>${book.title}</b>
        <div>${book.author}</div>
        <div class="availability-status" test-id="availability-status" style="color:${
          availabilityColors.txt
        };background-color:${availabilityColors.background}">${
      book.availability
    }</div>
        <div test-id="due-date">Due Date: <span${
          dueDate != "N/A" ? ` style="color:red"` : ""
        }>${dueDate}</span></div>
        <div test-id="hold-queue">Holders: ${noHolders ? "none" : "[" + book.holdQueue + "]"}</div>
        <button class="btn-small" onclick="handleCheckOutBtn(${i})">Check Out</button>
      </div>
      `;
  }
  document.getElementById("borrow-books-display").innerHTML = contents;
}

async function loadBorrowConfirmation(index) {
  const book = await getBookByIndex(index);
  const dueDateJson = await getApiJson("/api/potentialDueDate");

  document.getElementById(
    "confirmation-book-details-msg"
  ).innerHTML = `Do you want to borrow <b>${book.title}</b> by ${book.author}?`;

  document.getElementById(
    "confirmation-due-date-msg"
  ).innerHTML = `It would be <b>due</b> on <b>${dueDateJson.potentialDueDate}</b>`;

  document.getElementById("yes-btn").disabled = false;
}

async function loadHoldConfirmation(index) {
  // Get book with index
  const book = await getBookByIndex(index);

  // Display hold confirmation message
  const capacityRes = await getApiJson("/api/isAtBorrowingCapacity");
  let msg = `<b>${book.title}</b> is currently unavailable, would you like to place a <b>hold</b>?`;

  if (capacityRes.isAtBorrowingCapacity) {
    msg = `You are at the <b>3-book limit</b>, would you like to place a <b>hold</b> on <b>${book.title}</b>?`;
  }

  document.getElementById("place-hold-msg").innerHTML = msg;

  // Enable yes button after requests are finished
  document.getElementById("yes-btn").disabled = false;
}

async function loadReturnABook() {

  // Display user's currently borrowed books
  let contents = ``;

  const borrowedBooks = await getApiJson("/api/borrowed");
  for (let i = 0; i < borrowedBooks.length; ++i) {
    let book = borrowedBooks[i];
    contents += `
    <div class="book-card" test-id=${slugify(book.title)}>
       <b>${book.title}</b>
        <div>${book.author}</div>
        <div test-id="due-date">Due Date: <span style="color:red">${
          book.dueDate
        }</span></div>
        <button class="btn-small" onclick="handleReturnBookBtn(${i})">Return</button>
    </div>
    `;
  }
  document.getElementById("return-books-display").innerHTML = contents;
}

/* ============ Helpers ============ */
async function getSessionUsername() {
  const json = await getApiJson("/api/sessionUsername");
  return json.username;
}

async function getApiJson(api) {
  const response = await fetch(api);
  if (response.ok) {
    return await response.json();
  }
  return null;
}

async function postApiJson(api, body) {
  const response = await fetch(api, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  return await response.json();
}

async function getBookByIndex(index) {
  const allBooks = await getApiJson("/api/books");
  return allBooks[index];
}

async function getReturnBookByIndex(index) {
  const borrowedBooks = await getApiJson("/api/borrowed");
  return borrowedBooks[index];
}

function getAvailabilityStatusColor(status) {
  if (status == "Checked Out") return { txt: "red", background: "#FFD6D6" };
  if (status == "Available") return { txt: "green", background: "#D9FFD6" };
  if (status == "On Hold") return { txt: "orange", background: "#FFF1D1" };
  return "black";
}

function slugify(title) {
  return String(title)
    .normalize("NFKD") // decompose accents
    .replace(/[\u0300-\u036f]/g, "") // remove diacritics
    .toLowerCase()
    .trim()
    .replace(/&/g, "-and-") // optional: "&" -> "and"
    .replace(/[^a-z0-9]+/g, "-") // non-alnum -> hyphen
    .replace(/^-+|-+$/g, "") // trim leading/trailing hyphens
    .replace(/-+/g, "-"); // collapse multiple hyphens
}
