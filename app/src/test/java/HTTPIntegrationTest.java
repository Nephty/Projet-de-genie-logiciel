import app.Main;
import back.user.Bank;
import back.user.Portfolio;
import back.user.Profile;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author François VION
 */
public class HTTPIntegrationTest {

    private static final String testToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYXRhbiIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ5NjE1MTEyLCJ1c2VySWQiOiIxMjM0NTY3ODkifQ.5LP2W6CDGPCgjnbTlQZNv18u7JZtgcU4pjpu6xMooJA";

    @Test
    @DisplayName("Getting a user from api")
    public void getUser() {
        Main.setToken(testToken);
        assertDoesNotThrow(() -> {
            Profile userTest = new Profile("Carlos","Matos", "Matos0102031230", "English", "01.02.03-123.00");

            assertEquals("Elon", userTest.getFirstName());
            assertEquals("Musk", userTest.getLastName());
            assertEquals("123456789", userTest.getNationalRegistrationNumber());
        });

    }

    @Test
    @DisplayName("Getting a bank from api")
    public void getBank() {
        Main.setToken(testToken);
        assertDoesNotThrow(() -> {
            Bank bankTest = new Bank("ABCD");

            assertEquals("Belfius", bankTest.getName());
            assertEquals("ABCD", bankTest.getSwiftCode());
        });
    }

    @Test
    @DisplayName("Getting a portfolio from api")
    public void getPortfolio() {
        // This test verifies the requests : Portfolio, Wallet, Profile, Bank, Account, SubAccount, Transaction
        Main.setToken(testToken);
        assertDoesNotThrow(() -> {
            Portfolio portfolioTest = new Portfolio("123456789");

            assertEquals("Elon", portfolioTest.getUser().getFirstName());
            assertEquals("Musk", portfolioTest.getUser().getLastName());
            assertEquals("123456789", portfolioTest.getUser().getNationalRegistrationNumber());
            assertEquals("Belfius", portfolioTest.getWalletList().get(0).getBank().getName());
            assertEquals("0123456789ABCDEF", portfolioTest.getWalletList().get(0).getAccountList().get(0).getIBAN());
            assertEquals(1, portfolioTest.getWalletList().get(0).getAccountList().get(0).getSubAccountList().get(0).getTransactionHistory().get(0).getID());
        });
    }

    @Test
    @DisplayName("Login to an account")
    public void login() {
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "elonm")
                    .field("password", "igotalotofmoney")
                    .field("role", "ROLE_USER")
                    .asString();

            assertEquals(200, response.getStatus());
        });
    }

    @Test
    @DisplayName("Register a account")
    public void register() {
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/user")
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"username\": \"billG\",\r\n    \"userId\": \"01.01.01-001.01\",\r\n    \"email\": \"billGates@microsoft.com\",\r\n    \"password\": \"igotalotofmoney\",\r\n    \"firstname\": \"Bill\",\r\n    \"lastname\": \"Gates\",\r\n    \"language\": \"EN\"\r\n}")
                    .asString();

            assertEquals(201, response.getStatus());
        });

        // Login to get a token

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "billG")
                    .field("password", "igotalotofmoney")
                    .field("role", "ROLE_USER")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        if (response != null) {
            JSONObject obj = new JSONObject(response.getBody());

            // Delete the user to finish the test
            assertDoesNotThrow(() -> {
                Unirest.setTimeouts(0, 0);
                HttpResponse<String> response2 = Unirest.delete("https://flns-spring-test.herokuapp.com/api/user/01.01.01-001.01")
                        .header("Authorization", "Bearer " + obj.getString("access_token"))
                        .asString();
                assertEquals(200, response2.getStatus());
            });
        }

    }

    @Test
    @DisplayName("Change password")
    public void changePassword() {
        Main.setToken((testToken));
        // Current password is igotalotofmoney
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .body("{\r\n    \"username\": \"" + "elonm" + "\",\r\n    \"userID\": \"" + "123456789" + "\",\r\n    \"email\": \"" + "elon.musk@tesla.com" + "\",\r\n    \"password\": \"" + "igotnomoney" + "\",\r\n    \"firstname\": \"" + "Elon" + "\",\r\n    \"lastname\": \"" + "Musk" + "\",\r\n    \"language\": \"" + "EN US" + "\"\r\n}")
                    .asString();
        });

        // Try to log in to check if the password is well changed
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "elonm")
                    .field("password", "igotnomoney")
                    .field("role", "ROLE_USER")
                    .asString();

            assertEquals(200, response.getStatus());
        });

        // Set the password as before for future tests
        Unirest.setTimeouts(0, 0);
        try {
            Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .body("{\r\n    \"username\": \"" + "elonm" + "\",\r\n    \"userID\": \"" + "123456789" + "\",\r\n    \"email\": \"" + "elon.musk@tesla.com" + "\",\r\n    \"password\": \"" + "igotalotofmoney" + "\",\r\n    \"firstname\": \"" + "Elon" + "\",\r\n    \"lastname\": \"" + "Musk" + "\",\r\n    \"language\": \"" + "EN US" + "\"\r\n}")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
