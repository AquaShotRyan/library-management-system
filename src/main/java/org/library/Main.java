package org.library;

import java.io.PrintWriter;

public class Main {
    public static void main(String[] args){
        // setup
        Library library = new Library();
        LibraryInterface ui = new LibraryInterface();
        PrintWriter output = new PrintWriter(System.out);

        LibraryController controller = new LibraryController(library, ui, output);

        // main loop
        controller.run();

    }
}
