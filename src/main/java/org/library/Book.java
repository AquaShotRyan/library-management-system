package org.library;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.LinkedList;

public class Book {
    private BookDetails bookDetails;
    private Calendar dueDate;
    private Queue<User> holdQueue;

    Book(String title, String author){
        bookDetails = new BookDetails(title, author);
        dueDate = null;
        holdQueue = new LinkedList<>();
    }

    public void addHolder(User user){
        holdQueue.add(user);
    }

    public boolean containsHolder(User user){
        return holdQueue.contains(user);
    }

    public String getTitle(){ return bookDetails.getTitle(); }
    public String getAuthor() { return bookDetails.getAuthor(); }
    public Calendar getDueDate() {
        Calendar copy = (Calendar)dueDate.clone();
        return copy;
    }
    public int getHoldersNum(){ return holdQueue.size(); }
    public User getFirstHolder(){
        return holdQueue.peek();
    }

    public void setDueDate(Calendar date){ dueDate = date; }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
