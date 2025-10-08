package org.library;

import java.io.PrintWriter;
import java.util.Scanner;

public class LibraryController {
    private Library library;
    private LibraryInterface ui;

    LibraryController(Library library, LibraryInterface ui){
        this.library = library;
        this.ui = ui;
    }

    public void promptLogin(Scanner input, PrintWriter output){
        String u = ui.promptStringInput(input, output, "username: ");
        String p = ui.promptStringInput(input, output, "password: ");
        AuthEnum authResult = library.login(u, p);
        ui.displayAuthError(authResult, output);
    }

    public boolean isLoggedIn(){
        return library.getSessionUsername() != null;
    }

    public MenuEnum promptMenu(Scanner input, PrintWriter output){
        return ui.promptMenu(input, output);
    }
}
