package org.library;

import java.util.List;
import java.util.ArrayList;

public class BorrowedBooks {
    List<Book> books;

    public BorrowedBooks(){
        books = new ArrayList<>();
    }

    public Book getBookByTitle(String bookTitle){
        for (int i=0; i<size(); ++i){
            Book b = books.get(i);
            if (b.getTitle().equals(bookTitle))
                return b;
        }
        return null;
    }

    public void addBook(Book b){
        books.add(b);
    }

    public void removeBook(String bookTitle){
        for (int i=0; i<size(); ++i){
            Book b = books.get(i);
            if (b.getTitle().equals(bookTitle)){
                books.remove(i);
                return;
            }
        }
    }

    public List<Book> getBorrowedBooksList(){
        return books;
    }

    public int size(){ return books.size(); }
}
