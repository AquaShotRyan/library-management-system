package org.library;

import java.util.List;
import java.util.ArrayList;

public class BorrowedBooks {
    List<Book> books;

    public BorrowedBooks(){
        books = new ArrayList<>();
    }

    public Book getBookByTitle(String bookTitle){
        return new Book("null-title", "null-author");
    }

    public int size(){ return books.size(); }
}
