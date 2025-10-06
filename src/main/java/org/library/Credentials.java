package org.library;

import java.util.HashMap;

public class Credentials {
    private HashMap<String, Credential> credentials;

    public Credentials(){
        credentials = new HashMap<>();
    }

    public Credential getCredential(String username){
        return credentials.get(username);
    }

    public void addCredential(Credential c){
        credentials.put(c.getUsername(), c);
    }

}
