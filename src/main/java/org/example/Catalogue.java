package org.example;

import java.util.ArrayList;

public class Catalogue {
    ArrayList<Book> catalogue;

    public Catalogue(){
        catalogue = new ArrayList<Book>();
    }

    Book getBook(int index){
        return new Book("testBook", "testAuthor");
    }

    public int getCatalogueSize(){
        return catalogue.size();
    }
}
