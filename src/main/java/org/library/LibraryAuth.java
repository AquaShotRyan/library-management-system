package org.library;

public class LibraryAuth {
    private Borrowers users;

    public LibraryAuth(Borrowers borrowers){
        users = borrowers;
    }

    public AuthEnum authUser(String username, String password){
        if (!validateUsername(username) || !validatePassword(password)){
            return AuthEnum.INVALID_INPUT;
        }

        User user;
        try {
            user = users.getBorrower(username);

            if (!user.matchPassword(password))
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