package org.library;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Book {
    private BookDetails bookDetails;
    private Calendar dueDate;

    Book(String title, String author){
        bookDetails = new BookDetails(title, author);
        dueDate = null;
    }

    public String getTitle(){ return bookDetails.getTitle(); }
    public String getAuthor() { return bookDetails.getAuthor(); }
    public Calendar getDueDate() {
        Calendar copy = (Calendar)dueDate.clone();
        return copy;
    }
    public int getHoldersNum(){ return 0; }
    public User getFirstHolder(){
        return new User("null-username", "null-password");
    }

    public void setDueDate(Calendar date){ dueDate = date; }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
