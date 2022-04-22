import app.Main;
import back.user.Bank;
import back.user.ErrorHandler;
import back.user.Profile;
import back.user.Wallet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPIntegrationTest {

    @Test
    @DisplayName("Login to an account")
    @BeforeEach
    public void login() {
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "ABCDABCD")
                    .field("password", "ABCD")
                    .field("role", "ROLE_BANK")
                    .asString();
            JSONObject obj = new JSONObject(response.getBody());
            Main.setToken(obj.getString("access_token"));
            Main.setToken(obj.getString("refresh_token"));
            assertEquals(response.getStatus(), 200);
        });
    }

    @Test
    @DisplayName("Change password")
    public void changePassword() {
        // Current password is ABCD
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.put("https://flns-spring-test.herokuapp.com/api/bank")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"swift\": \"ABCDABCD\",\r\n    \"name\": \"Belfius\",\r\n    \"password\": \"CDBA\"\r\n}")
                    .asString();
            assertEquals(201, response.getStatus());
        });

        // Try to login to check if the password is well changed
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "ABCDABCD")
                    .field("password", "CDBA")
                    .field("role", "ROLE_BANK")
                    .asString();

            assertEquals(200, response.getStatus());
        });

        // Set the password as before for future tests
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.put("https://flns-spring-test.herokuapp.com/api/bank")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"swift\": \"ABCDABCD\",\r\n    \"name\": \"Belfius\",\r\n    \"password\": \"ABCD\"\r\n}")
                    .asString();
            assertEquals(201, response.getStatus());
        });
    }

    @Test
    @DisplayName("Getting a wallet for a user")
    public void getWallet() {
        // This test verify the requests : Wallet, Profile, Bank, Account, SubAccount, Transaction
        assertDoesNotThrow(() -> {
            Main.setBank(new Bank("ABCDABCD"));
            Wallet testWallet = new Wallet(new Profile("01.02.03-123.00"));
            assertEquals("Carlos", testWallet.getAccountUser().getFirstName());
            assertEquals("BE00000000000071", testWallet.getAccountList().get(0).getIBAN());
        });
    }

    @Test
    @DisplayName("Getting a bank from api")
    public void getBank() {
        assertDoesNotThrow(() -> {
            Bank bankTest = new Bank("ABCDABCD");

            assertEquals("Belfius", bankTest.getName());
            assertEquals("ABCDABCD", bankTest.getSwiftCode());
        });
    }

    @Test
    @DisplayName("Manage expired token")
    public void errorHandler(){
        // Sets an expired token
        String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBQkNEQUJDRCIsInJvbGUiOiJST0xFX0JBTksiLCJpc3MiOiJodHRwczovL2ZsbnMtc3ByaW5nLXRlc3QuaGVyb2t1YXBwLmNvbS9hcGkvdG9rZW4vcmVmcmVzaCIsImV4cCI6MTY1MDY1ODUyN30.euHh9jG7EhDu61P6X3ha0c1EY3gJaRkmAB9ECpb-UlY";
        Main.setToken(expiredToken);

        // Test a simple request
        assertDoesNotThrow(()->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/ABCDABCD")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .header("Content-Type", "application/json")
                    .asString();

            // Testing that the token is expired (error 412)
            assertEquals(response.getStatus(), 412);
        });

        // Test the same request with the same token but with the ErrorHandler class
        assertDoesNotThrow(()->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/ABCDABCD")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .header("Content-Type", "application/json")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });

            // Testing that the request worked
            assertEquals(response2.getStatus(), 200);
            // Testing that the access token changed
            assertNotEquals(expiredToken, Main.getToken());
        });
    }

    @Test
    @DisplayName("Getting a user from api")
    public void getUser() {
        assertDoesNotThrow(() -> {
            Profile userTest = new Profile("01.02.03-123.00");

            assertEquals("Carlos", userTest.getFirstName());
            assertEquals("Matos", userTest.getLastName());
            assertEquals("01.02.03-123.00", userTest.getNationalRegistrationNumber());
        });
    }
}

