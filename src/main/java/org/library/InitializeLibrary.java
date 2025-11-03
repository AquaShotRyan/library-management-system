package org.library;

public class InitializeLibrary {

    Catalogue catalogue = new Catalogue();
    Borrowers borrowers = new Borrowers();

    public Catalogue initCatalogue(){
        for (BookData book: BookData.values()){
            catalogue.addBook(new Book(book.getTitle(), book.getAuthor()));
        }
        return catalogue;
    }

    public Borrowers initBorrowers(){
        for (UserData user : UserData.values()){
            borrowers.addBorrower(new Borrower(user.getUsername(), user.getPassword()));
        }
        return borrowers;
    }
}
