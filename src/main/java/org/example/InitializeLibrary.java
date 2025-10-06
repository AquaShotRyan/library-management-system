package org.example;

public class InitializeLibrary {

    Catalogue catalogue = new Catalogue();
    Borrowers borrowers = new Borrowers();
    Credentials credentials = new Credentials();

    public Catalogue initializeLibrary(){
        catalogue.addBook(new Book("Great Gatsby", "F. Scott FitzGerald"));
        catalogue.addBook(new Book("Red Rising", "Pierce Brown"));
        catalogue.addBook(new Book("The Ways of Kings", "Brandon Sanderson"));
        catalogue.addBook(new Book("A Game of Thrones", "George R.R. Martin"));
        catalogue.addBook(new Book("The Handmaid's Tale", "Margaret Atwood"));
        catalogue.addBook(new Book("Nineteen Eighty-Four", "George Orwell"));
        catalogue.addBook(new Book("The Apothecary Diaries: Volume 1", "author7"));
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
        return catalogue;
    }

    public Borrowers initializeBorrowers(){
        borrowers.addBorrower(new Borrower("ryan"));
        borrowers.addBorrower(new Borrower("glorp"));
        borrowers.addBorrower(new Borrower("squeex"));
        return borrowers;
    }

    public Credentials initializeCredentials(){

        return credentials;
    }
}
