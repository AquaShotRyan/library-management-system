package org.library;

public class LibraryAuth {
    private Credentials credentials;

    public LibraryAuth(Credentials credentials){
        this.credentials = credentials;
    }

    public AuthEnum authUser(String username, String password){
        if (!validateUsername(username) || !validatePassword(password)){
            return AuthEnum.INVALID_INPUT;
        }

        Credential c;
        try {
            c = credentials.getCredential(username);

            if (!c.getPassword().equals(password))
                return AuthEnum.INVALID_CREDENTIALS;

        } catch (RuntimeException e) {
            return AuthEnum.INVALID_CREDENTIALS;
        }

        return AuthEnum.SUCCESS;
    }

    private boolean validateUsername(String u){
        return !(u.isEmpty());
    }
    private boolean validatePassword(String p){
        return !(p.isEmpty());
    }
}
