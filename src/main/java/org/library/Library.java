package org.library;

public class Library {
    private LibraryAuth auth;

    private String sessionUsername;

    public Library(){
        InitializeLibrary initLibrary = new InitializeLibrary();
        Credentials c = initLibrary.initializeCredentials();
        sessionUsername = null;

        auth = new LibraryAuth(c);
    }

    public AuthEnum login(String username, String password){
        AuthEnum authResult = auth.authUser(username, password);

        if (authResult == AuthEnum.SUCCESS)
            sessionUsername = username;

        return authResult;
    }

    public String getSessionUsername(){
        return sessionUsername;
    }

    public Book getBook(String title){
        return new Book("some_title", "some_author");
    }
}