import app.Main;
import back.user.Bank;
import back.user.Profile;
import back.user.Wallet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTest {

    private static String testToken;

    @BeforeAll
    public static void initialize(){
        // Login to get the right token
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "ABCD")
                    .field("password", "ABCD")
                    .field("role", "ROLE_BANK")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(response.getBody());
        testToken = obj.getString("access_token");
    }

    @Test
    @DisplayName("Login to an account")
    public void login(){
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "ABCD")
                    .field("password", "ABCD")
                    .field("role", "ROLE_BANK")
                    .asString();

            assertEquals(200, response.getStatus());
        });
    }

    @Test
    @DisplayName("Register an account")
    public void register(){
        // Register a new account
        assertDoesNotThrow(() ->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
                response = Unirest.post("https://flns-spring-test.herokuapp.com/api/bank")
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"swift\": \"NEWBANK\",\r\n    \"name\": \"TestBank\",\r\n    \"login\": \" \",\r\n    \"password\": \"1234\",\r\n    \"address\": \"44 Wall Street\",\r\n    \"country\": \"US\",\r\n    \"defaultCurrencyType\": 0\r\n}")
                        .asString();

                assertEquals(201, response.getStatus());
        });

        // Try to login to the account to get the token
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "NEWBANK")
                    .field("password", "1234")
                    .field("role", "ROLE_BANK")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(response.getBody());

        // Delete the account to finish the test

        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response2 = Unirest.delete("https://flns-spring-test.herokuapp.com/api/bank/NEWBANK")
                    .header("Authorization", "Bearer "+ obj.getString("access_token"))
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }


    @Test
    @DisplayName("Change password")
    public void changePassword(){
        // Current password is ABCD
        assertDoesNotThrow(() ->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.put("https://flns-spring-test.herokuapp.com/api/bank")
                    .header("Authorization", "Bearer "+ testToken)
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"swift\": \"ABCD\",\r\n    \"name\": \"Belfius\",\r\n    \"password\": \"ABDC\"\r\n}")
                    .asString();
            assertEquals(201, response.getStatus());
        }) ;

        // Try to login to check if the password is well changed
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "ABCD")
                    .field("password", "ABDC")
                    .field("role", "ROLE_BANK")
                    .asString();

            assertEquals(200, response.getStatus());
        });

        // Set the password as before for future tests
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response2 = Unirest.put("https://flns-spring-test.herokuapp.com/api/bank")
                    .header("Authorization", "Bearer "+ testToken)
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"swift\": \"ABCD\",\r\n    \"name\": \"Belfius\",\r\n    \"password\": \"ABCD\"\r\n}")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Getting a wallet for a user")
    public void getWallet(){
        Main.setToken(testToken);
        // This test verify the requests : Wallet, Profile, Bank, Account, SubAccount, Transaction
        assertDoesNotThrow(() ->{
            Main.setBank(new Bank("ABCD"));
            Wallet testWallet = new Wallet(new Profile("123456789"));
            System.out.println(testWallet.getAccountUser().getFirstName());
            System.out.println(testWallet.getAccountList().get(0).getIBAN());
            assertEquals("Elon", testWallet.getAccountUser().getFirstName());
            assertEquals("0123456789ABCDEF", testWallet.getAccountList().get(0).getIBAN());
            assertEquals(1, testWallet.getAccountList().get(0).getSubAccountList().get(0).getTransactionHistory().get(0).getID());
        });
    }
}

