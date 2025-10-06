package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceTest {

    @Nested
    @DisplayName("RESP-03: prompting username and password")
    public class PromptCredentialsTest{
        private LibraryInterface libraryInterface;
        private StringWriter output;

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @BeforeEach
        void initOutput(){
            output = new StringWriter();
        }

        @Test
        @DisplayName("Check username is prompted")
        void RESP_03_test_1(){
            Scanner input = new Scanner("some_username");

            libraryInterface.promptStringInput(input, new PrintWriter(output), "username: ");

            assertTrue(output.toString().contains("username:"));
        }

        @Test
        @DisplayName("Check password is prompted")
        void RESP_03_test_2(){
            Scanner input = new Scanner("some_password");

            libraryInterface.promptStringInput(input, new PrintWriter(output), "password: ");

            assertTrue(output.toString().contains("password:"));
        }

        @Test
        @DisplayName("Check input 'glorp' was received from the prompt")
        void RESP_03_test_3(){
            String username = "glorp";
            Scanner input = new Scanner(username);

            String result = libraryInterface.promptStringInput(input, new PrintWriter(output), "username: ");

            assertEquals(username, result);
        }
    }

}

