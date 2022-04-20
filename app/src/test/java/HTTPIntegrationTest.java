import app.Main;
import back.user.Bank;
import back.user.Profile;
import back.user.Wallet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

