package org.library;

public class InitializeLibrary {

    Catalogue catalogue = new Catalogue();
    Borrowers borrowers = new Borrowers();

    public Catalogue initCatalogue(){
        catalogue.addBook(new Book("Great Gatsby", "F. Scott FitzGerald"));
        catalogue.addBook(new Book("Red Rising", "Pierce Brown"));
        catalogue.addBook(new Book("The Ways of Kings", "Brandon Sanderson"));
        catalogue.addBook(new Book("A Game of Thrones", "George R.R. Martin"));
        catalogue.addBook(new Book("The Handmaid's Tale", "Margaret Atwood"));
        catalogue.addBook(new Book("Nineteen Eighty-Four", "George Orwell"));
        catalogue.addBook(new Book("The Apothecary Diaries: Volume 1", "Natsu Hyuga"));
        catalogue.addBook(new Book("The Science of Beauty", "Michelle Wong"));
        catalogue.addBook(new Book("No Longer Human", "Osamu Dazai"));
        catalogue.addBook(new Book("Eragon", "Christopher Paolini"));
        catalogue.addBook(new Book("Moby-Dick", "Herman Melville"));
        catalogue.addBook(new Book("Supergirl: Woman of Tomorrow #1", "Tom King"));
        catalogue.addBook(new Book("Absolute Batman #1", "Scott Snyder"));
        catalogue.addBook(new Book("Berserk Deluxe Volume 1", "Kentaro Miura"));
        catalogue.addBook(new Book("To Kill a Mockingbird", "Harper Lee"));
        catalogue.addBook(new Book("The Art and Making of Arcane", "Elisabeth Vincentelli"));
        catalogue.addBook(new Book("The Hunger Games", "Suzanne Collins"));
        catalogue.addBook(new Book("Blood Meridian", "Cormac McCarthy"));
        catalogue.addBook(new Book("Crime and Punishment", "Fyodor Dostoevsky"));
        catalogue.addBook(new Book("2001: A Space Odyssey", "Arthur C. Clarke"));

        catalogue.addBook(new Book("The Great Gatsby", "F. Scott FitzGerald"));
        catalogue.addBook(new Book("1984", "George Orwell"));
        catalogue.addBook(new Book("Pride and Prejudice", "Jane Austen"));
        catalogue.addBook(new Book("The Hobbit", "J. R. R. Tolkien"));
        catalogue.addBook(new Book("Harry Potter", "J. K. Rowling"));
        catalogue.addBook(new Book("The Catcher in the Rye", "J. D. Salinger"));
        catalogue.addBook(new Book("Animal Farm", "George Orwell"));
        catalogue.addBook(new Book("Lord of the Flies", "William Golding"));
        catalogue.addBook(new Book("Jane Eyre", "Charlotte Brontë"));
        catalogue.addBook(new Book("Wuthering Heights", "Emily Brontë"));
        catalogue.addBook(new Book("The Odyssey", "Homer"));
        catalogue.addBook(new Book("Hamlet", "William Shakespeare"));
        catalogue.addBook(new Book("War and Peace", "Leo Tolstoy"));
        catalogue.addBook(new Book("The Divine Comedy", "Dante Alighieri"));
        catalogue.addBook(new Book("Don Quixote", "Miguel de Cervantes"));
        catalogue.addBook(new Book("The Iliad", "Homer"));
        catalogue.addBook(new Book("Ulysses", "James Joyce"));
        return catalogue;
    }

    public Borrowers initBorrowers(){
        borrowers.addBorrower(new Borrower("ryan", "password123"));
        borrowers.addBorrower(new Borrower("glorp", "alien"));
        borrowers.addBorrower(new Borrower("squeex", "iambald"));
        borrowers.addBorrower(new Borrower("alice", "pass123"));
        borrowers.addBorrower(new Borrower("bob", "pass456"));
        borrowers.addBorrower(new Borrower("charlie", "pass789"));
        return borrowers;
    }
}
