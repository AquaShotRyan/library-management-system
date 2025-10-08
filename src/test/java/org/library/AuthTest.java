package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

        @ParameterizedTest
        @CsvSource({
                "squeex,wrong_password",
                "non-existent_user,some_password"
        })
        @DisplayName("Non-existent username or password, returns AuthEnum.INVALID_CREDENTIALS")
        void RESP_04_test_2(String username, String password){
            AuthEnum result = libraryAuth.authUser(username, password);

            assertEquals(AuthEnum.INVALID_CREDENTIALS, result);
        }

        @ParameterizedTest
        @CsvSource({
                "'',iambald",
                "squeex,''"
        })
        @DisplayName("Username or password is valid and exists, but one is blank, returns AuthEnum.INVALID_INPUT")
        void RESP_04_test_3(String username, String password){
            AuthEnum result = libraryAuth.authUser(username, password);

            assertEquals(AuthEnum.INVALID_INPUT, result);
        }

        @Test
        @DisplayName("If validator returns AuthError.INVALID_CREDENTIALS, display 'ERROR: credentials not found'")
        void RESP_04_test_4(){
            AuthEnum error = AuthEnum.INVALID_CREDENTIALS;
            StringWriter output = new StringWriter();

            libraryInterface.displayAuthError(error, new PrintWriter(output));
            assertTrue(output.toString().contains("ERROR: credentials not found"));
        }

        @Test
        @DisplayName("If validator returns AuthError.INVALID_INPUT, display 'ERROR: invalid input'")
        void RESP_04_test_5(){
            AuthEnum error = AuthEnum.INVALID_INPUT;
            StringWriter output = new StringWriter();

            libraryInterface.displayAuthError(error, new PrintWriter(output));
            assertTrue(output.toString().contains("ERROR: invalid input"));
        }
    }

    @Nested
    @DisplayName("RESP-05: create and maintain session for authenticated user")
    public class CreateSessionTest {
        Library library;

        @BeforeEach
        void initLibrary(){

            library = new Library();
        }

        @Test
        @DisplayName("Retrieve username (session) 'ryan' from library after login")
        void RESP_05_test_1(){
            String username = "ryan";
            String password = "password123";

            AuthEnum authResult = library.login(username, password);

            assertEquals(username, library.getSessionUsername());
        }

        @Test
        @DisplayName("Retrieve null (session) from library after unsuccessful login")
        void RESP_05_test_2(){
            String username = "ryan";
            String password = "wrong_password";

            AuthEnum authResult = library.login(username, password);

            assertNull(library.getSessionUsername());
        }
    }
}

