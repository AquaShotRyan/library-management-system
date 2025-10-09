package org.library;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


public class Catalogue {
    HashMap<String, Book> catalogue;

    public Catalogue(){
        catalogue = new HashMap<>();
    }

    public void addBook(Book book){
        catalogue.put(book.getTitle(), book);
    }

    Book getBook(String title){
        return catalogue.get(title);
    }

    public int getCatalogueSize(){
        return catalogue.size();
    }

    public List<Book> getAllBooks(){
        List<Book> books = new ArrayList<>();
        catalogue.forEach((k, v) -> {
            books.add(v);
        });
        return books;
    }
}
