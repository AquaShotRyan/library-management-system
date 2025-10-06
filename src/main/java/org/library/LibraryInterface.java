package org.library;

import java.util.Scanner;
import java.io.PrintWriter;

public class LibraryInterface {
    public String promptStringInput(Scanner input, PrintWriter output, String prompt){
        output.println(prompt); output.flush();
        String inputStr = input.nextLine();

        return inputStr;
    }

    public void displayAuthError(AuthEnum error, PrintWriter output){
        if (error == AuthEnum.INVALID_CREDENTIALS){
            output.println("ERROR: credentials not found");
            output.flush();
        }else if(error == AuthEnum.INVALID_INPUT){
            output.println("ERROR: invalid input");
            output.flush();
        }
    }
}
