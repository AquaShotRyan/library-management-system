package org.library;

import java.util.ArrayList;

public class Borrowers {
    private ArrayList<Borrower> borrowers;

    public Borrowers(){
        borrowers = new ArrayList<>();
    }

    public void addBorrower(Borrower b){
        borrowers.add(b);
    }

    public Borrower getBorrower(int index) {
        return borrowers.get(index);
    }

    public int getBorrowersSize() {return borrowers.size();}
}
