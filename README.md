## Setup Instructions

1. Clone the project and change the branch to `A3-Cypress-Tran`

```
git clone -b A3-Cypress-Tran https://github.com/AquaShotRyan/A1-Tran-Ryan-101236202.git
```

2. Open a terminal in the root folder and run:

```
npm install
```

## Running the Application

1. Start the server (in terminal in root folder)

```
npm start
```

or

```
node server.js
```

2. Open http://localhost:3000/

## Running Cypress Test

First start the server using `npm start`

### Headless Mode

```
npm run cy:run
```

### Headed Mode

```
npm run cy:open
```

## AI Usage

- What AI was used: **Github Copilot** (VSCode extension)
  - Model that Github Copilot used was set to `auto`
  - Mix of GPT-4.1, GPT-4o, GPT-5 mini, Raptor mini, and Claude Haiku 4.5
- Where AI was used:
  - Converting `.java` files to `.js` files (i.e. Library, Borrower, User, Book, etc.)
  - `/api/` routes and express setup
    - Used AI to initially make the routes
    - Modified the routes myself afterward
  - Debugging when making the web app
  - Some CSS
