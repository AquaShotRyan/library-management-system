package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    @Nested
    @DisplayName("RESP-04: verifying and validating credentials")
    public class CredentialsTest {
        private LibraryAuth libraryAuth;
        private LibraryInterface libraryInterface;

        @BeforeEach
        void initLibraryAuth(){
            InitializeLibrary library = new InitializeLibrary();
            Credentials c = library.initializeCredentials();
            libraryAuth = new LibraryAuth(c);
        }

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @Test
        @DisplayName("Existing username and password input returns AuthEnum.SUCCESS")
        void RESP_04_test_1(){
            String username = "squeex";
            String password = "iambald";

            AuthEnum result = libraryAuth.authUser(username, password);
            assertEquals(AuthEnum.SUCCESS, result);
        }

        @Test
        @DisplayName("Existing username but wrong password, returns AuthEnum.INVALID_CREDENTIALS")
        void RESP_04_test_2(){
            String username = "squeex";
            String password = "wrong_password";

            AuthEnum result = libraryAuth.authUser(username, password);
            assertEquals(AuthEnum.INVALID_CREDENTIALS, result);
        }

        @Test
        @DisplayName("Username and password are valid, but don't exist, returns AuthEnum.INVALID_CREDENTIALS")
        void RESP_04_test_3(){
            String username = "non-existent_user";
            String password = "some_password";

            AuthEnum result = libraryAuth.authUser(username, password);
            assertEquals(AuthEnum.INVALID_CREDENTIALS, result);
        }

        @Test
        @DisplayName("Password is valid and exists, but username is blank, returns AuthEnum.INVALID_INPUT")
        void RESP_04_test_4(){
            String username = "";
            String password = "iambald";

            AuthEnum result = libraryAuth.authUser(username, password);
            assertEquals(AuthEnum.INVALID_INPUT, result);
        }

        @Test
        @DisplayName("Username is valid and exists, but password is blank, returns AuthEnum.INVALID_INPUT'")
        void RESP_04_test_5(){
            String username = "squeex";
            String password = "";

            AuthEnum result = libraryAuth.authUser(username, password);
            assertEquals(AuthEnum.INVALID_INPUT, result);
        }

        @Test
        @DisplayName("If validator returns AuthError.INVALID_CREDENTIALS, display 'ERROR: credentials not found'")
        void RESP_04_test_6(){
            AuthEnum error = AuthEnum.INVALID_CREDENTIALS;
            StringWriter output = new StringWriter();

            libraryInterface.displayAuthError(error, new PrintWriter(output));
            assertTrue(output.toString().contains("ERROR: credentials not found"));
        }

        @Test
        @DisplayName("If validator returns AuthError.INVALID_INPUT, display 'ERROR: invalid input'")
        void RESP_04_test_7(){
            AuthEnum error = AuthEnum.INVALID_INPUT;
            StringWriter output = new StringWriter();

            libraryInterface.displayAuthError(error, new PrintWriter(output));
            assertTrue(output.toString().contains("ERROR: invalid input"));
        }
    }
}

