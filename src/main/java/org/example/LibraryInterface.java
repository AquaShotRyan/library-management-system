package org.example;

import java.util.Scanner;
import java.io.PrintWriter;

public class LibraryInterface {
    public String promptStringInput(Scanner input, PrintWriter output, String prompt){
        output.println(prompt); output.flush();
        String inputStr = input.nextLine();

        return inputStr;
    }

    public void displayAuthError(AuthEnum error, PrintWriter output){
        return;
    }
}
