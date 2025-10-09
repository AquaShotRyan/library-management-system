package org.library;

public class BookDetails {
    private final String title;
    private final String author;

    public BookDetails(String title, String author){
        this.title = title;
        this.author = author;
    }

    public String getTitle(){
        return title;
    }
    public String getAuthor(){
        return author;
    }
}
