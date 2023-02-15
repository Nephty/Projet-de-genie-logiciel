import app.Main;
import back.user.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
            Main.setRefreshToken(obj.getString("refresh_token"));
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
        // This test verify the requests : Wallet, Profile, Bank, Account, SubAccount
        assertDoesNotThrow(() -> {
            Main.setBank(new Bank("ABCDABCD"));
            Wallet testWallet = new Wallet(new Profile("01.02.03-123.00"));
            assertEquals(testWallet.getAccountUser().getFirstName(), "Carlos");
            assertEquals(testWallet.getAccountList().size(), 2);
            assertEquals(testWallet.getAccountList().get(0).getIBAN(), "BE00002305000000");
            assertEquals(testWallet.getAccountList().get(0).getSubAccountList().get(0).getAmount(), 0.0);
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

    @Test
    @DisplayName("Getting request from api")
    public void fetchRequest(){
        assertDoesNotThrow(()->{
            ArrayList<Request> reqList = Request.fetchRequests(0);
            ArrayList<Request> reqList2 = Request.fetchRequests(3);
            ArrayList<Request> reqList3 = Request.fetchRequests(6);

            assertEquals(reqList.size(), 3);
            assertEquals(reqList2.size(), 1);
            assertEquals(reqList3.size(), 0);
            assertEquals(reqList.get(0).getReason(), CommunicationType.CREATE_ACCOUNT);
            assertEquals(reqList2.get(0).getReason(), CommunicationType.NEW_WALLET);
            assertEquals(reqList.get(0).getSenderID(), "00.00.00-000.00");
            assertEquals(reqList.get(0).getDate(), "2022-04-25");
        });
    }

    @Test
    @DisplayName("Create account list")
    public void createAccountList(){
        Main.setBank(new Bank("ABCDABCD", "Belfius"));
        // Testing active accounts
        String json = "[{\"accountId\":\"BE00002305000000\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE00002305000000\",\"swift\":\"ABCDABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCDABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-23\",\"deleted\":false}},{\"accountId\":\"BE01020300000000\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE01020300000000\",\"swift\":\"BEGLGLGL\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"BEGLGLGL\",\"name\":\"UwU\",\"password\":null,\"address\":\"Tournai\",\"country\":\"Mons\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-22\",\"deleted\":false}}]";
        ArrayList<Account> accountList = Wallet.createsAccountList(json);
        assertEquals(accountList.size(), 1);
        assertEquals(accountList.get(0).getIBAN(), "BE00002305000000");
        assertEquals(accountList.get(0).isArchived(), false);
        assertEquals(accountList.get(0).isActivated(), true);
        assertEquals(accountList.get(0).getAccountOwner().getNationalRegistrationNumber(), "01.02.03-123.00");

        // Testing deleted accounts
        String json2 = "[{\"accountId\":\"BE00000000000071\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE00000000000071\",\"swift\":\"ABCDABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCDABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-10\",\"deleted\":true}}]";
        ArrayList<Account> accountList2 = Wallet.createsAccountList(json2);
        assertEquals(accountList2.size(), 1);
        assertEquals(accountList2.get(0).getIBAN(), "BE00000000000071");
        assertEquals(accountList2.get(0).isArchived(), true);
        assertEquals(accountList2.get(0).isActivated(), true);
        assertEquals(accountList2.get(0).getAccountOwner().getNationalRegistrationNumber(), "01.02.03-123.00");
    }
}

