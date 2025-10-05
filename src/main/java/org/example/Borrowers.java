package org.example;

import java.util.ArrayList;

public class Borrowers {
    private ArrayList<Borrower> borrowers;

    public Borrowers(){
        borrowers = new ArrayList<>();
    }

    public void addBorrower(Borrower b){
        return;
    }

    public Borrower getBorrower(int index) {
        return new Borrower("nullUsername");
    }

    public int getBorrowersSize() {return borrowers.size();}
}
