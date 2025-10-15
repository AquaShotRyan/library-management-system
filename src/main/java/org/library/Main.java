package org.library;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        // setup
        Library library = new Library();
        LibraryInterface ui = new LibraryInterface();
        Scanner input = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out);

        LibraryController controller = new LibraryController(library, ui, input, output);

        // main loop
        controller.run();

    }
}
