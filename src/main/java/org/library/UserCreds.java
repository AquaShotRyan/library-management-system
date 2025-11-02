package org.library;

public enum UserCreds {
    RYAN("ryan", "password123"),
    GLORP("glorp", "alien"),
    SQUEEX("squeex", "iambald"),
    ALICE("alice", "pass123"),
    BOB("bob", "pass456"),
    CHARLIE("charlie", "pass789");

    private String username;
    private String password;

    private UserCreds(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){ return username; }
    public String getPassword(){ return password; }
}
