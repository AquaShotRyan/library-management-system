package org.library;

public class BorrowTransaction {
    private final String bookTitle;
    private final String borrower;
    private final String borrowDate;

    public BorrowTransaction(String title, String borrowerName, String date){
        bookTitle = title;
        borrower = borrowerName;
        borrowDate = date;
    }

    public String getBookTitle(){
        return bookTitle;
    }

    public String getBorrower(){
        return borrower;
    }

    public String getBorrowDate(){
        return borrowDate;
    }
}
