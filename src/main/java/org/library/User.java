package org.library;

public class User {
    private String username;
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean matchPassword(String p){
        return password.equals(p);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof User))
            return false;
        User user = (User) obj;

        return user.getUsername().equals(username);
    }
}
