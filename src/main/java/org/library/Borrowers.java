package org.library;

import java.util.HashMap;

public class Borrowers {
    private HashMap<String, Borrower> borrowers;

    public Borrowers(){
        borrowers = new HashMap<>();
    }

    public void addBorrower(Borrower b){
        borrowers.put(b.getUsername(), b);
    }

    public Borrower getBorrower(String username) {
        return borrowers.get(username);
    }

    public int getBorrowersSize() {return borrowers.size();}
}
