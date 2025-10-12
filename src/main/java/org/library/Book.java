package org.library;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.LinkedList;

public class Book {
    private BookDetails bookDetails;
    private Calendar dueDate;
    private Queue<User> holdQueue;
    private User curHolder;

    Book(String title, String author){
        bookDetails = new BookDetails(title, author);
        dueDate = null;
        holdQueue = new LinkedList<>();
        curHolder = null;
    }

    public void addHolder(User user){
        holdQueue.add(user);
    }

    public User popHolder(){
        return holdQueue.poll();
    }

    public boolean containsHolder(User user){
        return holdQueue.contains(user);
    }

    public boolean hasBorrower(){
        return true;
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
    public User getCurHolder(){ return curHolder; }
    public BookDetails getBookDetails(){ return bookDetails; }
    public User getCurBorrower(){ return new User("null-username", "null-password"); }

    public void setDueDate(Calendar date){ dueDate = date; }
    public void setCurHolder(User holder){ curHolder = holder; }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
