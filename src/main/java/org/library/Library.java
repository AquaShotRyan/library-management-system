package org.library;

public class Library {
    private LibraryAuth auth;

    private String sessionUsername;

    public Library(){
        InitializeLibrary initLibrary = new InitializeLibrary();
        Credentials c = initLibrary.initializeCredentials();

        auth = new LibraryAuth(c);
    }

    public AuthEnum login(String username, String password){
        return null;
    }

    public String getSessionUsername(){
        return "null-session";
    }
}