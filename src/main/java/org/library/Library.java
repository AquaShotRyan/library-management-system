package org.library;

public class Library {
    private Catalogue catalogue;
    private Borrowers borrowers;

    private LibraryAuth auth;
    private Borrower sessionBorrower;

    public Library(){
        InitializeLibrary initLibrary = new InitializeLibrary();
        catalogue = initLibrary.initCatalogue();
        borrowers = initLibrary.initBorrowers();

        auth = new LibraryAuth(borrowers);
        sessionBorrower = null;
    }

    public AuthEnum login(String username, String password){
        AuthEnum authResult = auth.authUser(username, password);

        if (authResult == AuthEnum.SUCCESS)
            sessionBorrower = borrowers.getBorrower(username);

        return authResult;
    }

    public String getSessionUsername(){
        if (sessionBorrower == null) return null;
        return sessionBorrower.getUsername();
    }

    public Book getBook(String title){
        return catalogue.getBook(title);
    }
}