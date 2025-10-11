package org.library;

import java.util.Scanner;
import java.io.PrintWriter;

public class LibraryInterface {
    public String promptStringInput(Scanner input, PrintWriter output, String prompt){
        output.println(prompt); output.flush();
        String inputStr = input.nextLine();

        return inputStr;
    }

    public MenuEnum promptMenu(Scanner input, PrintWriter output){
        // display menu options
        for (MenuEnum o: MenuEnum.values()){
            if (o != MenuEnum.INVALID_INPUT){
                output.println(o.getFullOptionDesc());
                output.flush();
            }
        }
        // get and return user input
        String inputStr = input.nextLine();
        int inputNum = -1;

        try {
            inputNum = Integer.parseInt(inputStr);
        } catch (NumberFormatException e){
            output.println("ERROR: invalid input"); output.flush();
        }
        if (inputNum < 1 || inputNum > 3)
            output.println("ERROR: invalid input"); output.flush();

        return MenuEnum.getOption(inputNum);
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
    public void displayAvailableBookNotification(PrintWriter output, Book b){
        String msg = String.format("NOTIFICATION: %s is available!", b.toString());
        output.println(msg);
        output.flush();
    }

    public boolean promptConfirmation(Scanner input, PrintWriter output, String msg){
        if (msg.isBlank()){
            output.println("(y/n): "); output.flush();
        }else{
            output.println(String.format("%s (y/n): ", msg)); output.flush();
        }

        String inputStr = input.nextLine();

        return inputStr.equals("y");
    }

    public void displayNumberOfBorrowedBooks(PrintWriter output, int n){
        return;
    }

}
