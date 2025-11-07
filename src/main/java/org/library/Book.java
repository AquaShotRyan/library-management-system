package org.library;

import java.util.Queue;
import java.util.LinkedList;

public class Book {
    private BookDetails bookDetails;
    private LibraryDate dueDate;
    private Queue<User> holdQueue;
    private User curHolder;
    private User curBorrower;

    Book(String title, String author){
        bookDetails = new BookDetails(title, author);
        dueDate = null;
        holdQueue = new LinkedList<>();
        curHolder = null;
        curBorrower = null;
    }

    public void addHolder(User user){
        holdQueue.add(user);
    }

    public User popHolder(){
        return holdQueue.poll();
    }

    public User removeCurHolder(){
        User holder = curHolder;
        curHolder = null;
        return holder;
    }

    public User removeCurBorrower(){
        User borrower = curBorrower;
        curBorrower = null;
        return borrower;
    }

    public AvailabilityEnum getAvailabilityStatus(String username){
        if (hasHolder() && !hasBorrower() && curHolder.getUsername().equals(username))
            return AvailabilityEnum.AVAILABLE;
        if (hasBorrower())
            return AvailabilityEnum.CHECKED_OUT;
        if (hasHolder())
            return AvailabilityEnum.ON_HOLD;
        return AvailabilityEnum.AVAILABLE;
    }

    // getters
    public String getTitle(){ return bookDetails.getTitle(); }
    public String getAuthor() { return bookDetails.getAuthor(); }
    public LibraryDate getDueDate() {
        if (dueDate == null) return null;
        return new LibraryDate(dueDate);
    }

    public int getHoldersNum(){ return holdQueue.size(); }
    public User peekHolderQueue(){
        return holdQueue.peek();
    }
    public User getCurHolder(){ return curHolder; }
    public BookDetails getBookDetails(){ return bookDetails; }
    public User getCurBorrower(){ return curBorrower; }
    public String getDueDateStr(){
        if (dueDate == null) return "N/A";
        return dueDate.toString();
    }

    // setters
    public void setDueDate(LibraryDate date){ dueDate = date; }
    public void setCurHolder(User holder){ curHolder = holder; }
    public void setCurBorrower(User borrower){ curBorrower = borrower; }

    // booleans
    public boolean containsHolder(User user){ return holdQueue.contains(user); }
    public boolean hasBorrower(){ return curBorrower != null; }
    public boolean hasHolder(){ return curHolder != null; }
    public boolean curBorrowerIs(String username){
        if (!hasBorrower()) return false;
        return username.equals(curBorrower.getUsername());
    }
    public boolean curHolderIs(String username){
        if (!hasHolder()) return false;
        return username.equals(curHolder.getUsername());
    }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
