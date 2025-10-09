package org.library;

public class Book {
    private BookDetails bookDetails;

    Book(String title, String author){
        bookDetails = new BookDetails(title, author);
    }

    public String getTitle(){ return bookDetails.getTitle(); }
    public String getAuthor() { return bookDetails.getAuthor(); }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
