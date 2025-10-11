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
        return new GregorianCalendar(1900, 1, 1);
    }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
