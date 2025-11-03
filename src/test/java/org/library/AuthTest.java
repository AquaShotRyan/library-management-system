package org.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    @Nested
    @DisplayName("RESP-04: verifying and validating credentials")
    public class CredentialsValidation {
        private LibraryAuth libraryAuth;
        private LibraryInterface libraryInterface;

        @BeforeEach
        void initLibraryAuth(){
            InitializeLibrary library = new InitializeLibrary();
            Borrowers borrowers = library.initBorrowers();
            libraryAuth = new LibraryAuth(borrowers);
        }

        @BeforeEach
        void initLibraryInterface(){
            libraryInterface = new LibraryInterface();
        }

        @Test
        @DisplayName("Existing username and password input returns AuthEnum.SUCCESS")
        void RESP_04_test_1(){
            String username = UserData.SQUEEX.getUsername();
            String password = UserData.SQUEEX.getPassword();

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
    public class CreateSession {
        Library library;

        @BeforeEach
        void initLibrary(){
            library = new Library();
        }

        @Test
        @DisplayName("Retrieve username (session) 'ryan' from library after login")
        void RESP_05_test_1(){
            String username = UserData.RYAN.getUsername();
            String password = UserData.RYAN.getPassword();

            AuthEnum authResult = library.login(username, password);

            assertEquals(username, library.getSessionUsername());
        }

        @Test
        @DisplayName("Retrieve null (session) from library after unsuccessful login")
        void RESP_05_test_2(){
            String username = UserData.RYAN.getUsername();
            String password = "wrong_password";

            AuthEnum authResult = library.login(username, password);

            assertNull(library.getSessionUsername());
        }
    }

    @Nested
    @DisplayName("RESP-10: delete session on logout")
    public class DeleteSession {
        @Test
        @DisplayName("After logging in and logging out, session should be null")
        void RESP_10_test_1(){
            Library library = new Library();
            library.login(UserData.SQUEEX.getUsername(), UserData.SQUEEX.getPassword());
            library.logout();

            assertNull(library.getSessionUsername());
        }
    }
}

