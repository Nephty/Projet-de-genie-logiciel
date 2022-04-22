import app.Main;
import back.user.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author François VION
 */
public class HTTPIntegrationTest {

    @Test
    @DisplayName("Login to an account")
    @BeforeEach
    public void login() {
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "Matos01.02.03-123.00")
                    .field("password", "bitconnect")
                    .field("role", "ROLE_USER")
                    .asString();
            JSONObject obj = new JSONObject(response.getBody());
            Main.setToken(obj.getString("access_token"));
            Main.setRefreshToken(obj.getString("refresh_token"));
            assertEquals(200, response.getStatus());
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
    @DisplayName("Getting a bank from api")
    public void getBank() {
        assertDoesNotThrow(() -> {
            Bank bankTest = new Bank("ABCDABCD");

            assertEquals("Belfius", bankTest.getName());
            assertEquals("ABCDABCD", bankTest.getSwiftCode());
        });
    }

    @Test
    @DisplayName("Getting a portfolio from api")
    public void getPortfolio() {
        Main.setUser(new Profile("01.02.03-123.00"));
        // This test verifies the requests : Portfolio, Wallet, Profile, Bank, Account, SubAccount, Transaction
        assertDoesNotThrow(() -> {
            Portfolio portfolioTest = new Portfolio("01.02.03-123.00");

            assertEquals("Carlos", portfolioTest.getUser().getFirstName());
            assertEquals("Matos", portfolioTest.getUser().getLastName());
            assertEquals("01.02.03-123.00", portfolioTest.getUser().getNationalRegistrationNumber());
            assertEquals("BEGLGLGL", portfolioTest.getWalletList().get(0).getBank().getSwiftCode());
            assertEquals("BE01020300000000", portfolioTest.getWalletList().get(0).getAccountList().get(0).getIBAN());
            assertEquals(39, portfolioTest.getWalletList().get(0).getAccountList().get(0).getSubAccountList().get(0).getTransactionHistory().get(0).getID());
            assertEquals("2022-04-22", portfolioTest.getWalletList().get(0).getAccountList().get(0).getSubAccountList().get(0).getTransactionHistory().get(0).getSendingDate());
        });
    }


    @Test
    @DisplayName("Change password")
    public void changePassword() {
        // Current password is bitconnect
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .body("{\r\n    \"username\": \"" + "Matos01.02.03-123.00" + "\",\r\n    \"userID\": \"" + "01.02.03-123.00" + "\",\r\n    \"email\": \"" + "carlosamatos@gmail.com" + "\",\r\n    \"password\": \"" + "moneymoney" + "\",\r\n    \"firstname\": \"" + "Carlos" + "\",\r\n    \"lastname\": \"" + "Matos" + "\",\r\n    \"language\": \"" + "French (Belgium)" + "\"\r\n}")
                    .asString();
        });

        // Try to log in to check if the password is well changed
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "Matos01.02.03-123.00")
                    .field("password", "moneymoney")
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
                    .body("{\r\n    \"username\": \"" + "Matos01.02.03-123.00" + "\",\r\n    \"userID\": \"" + "01.02.03-123.00" + "\",\r\n    \"email\": \"" + "carlosamatos@gmail.com" + "\",\r\n    \"password\": \"" + "bitconnect" + "\",\r\n    \"firstname\": \"" + "Carlos" + "\",\r\n    \"lastname\": \"" + "Matos" + "\",\r\n    \"language\": \"" + "French (Belgium)" + "\"\r\n}")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Getting all notifications")
    public void getNotifications() {
        assertDoesNotThrow(() -> {
            ArrayList<Notification> notifList = Notification.fetchCustomNotification();
            assertEquals(notifList.size(), 1);
            assertEquals(notifList.get(0).getID(), 84);
            assertEquals(notifList.get(0).getDate(), "2022-04-20");
            assertEquals(notifList.get(0).getContent(), "The bank BNP hasn't created you a new account");
        });
    }

    @Test
    @DisplayName("Change the flag")
    public void changeFlag(){
        // Fetch a notification
        Notification notifTest = Notification.fetchCustomNotification().get(0);

        boolean flagged = notifTest.isFlagged();
        assertEquals(notifTest.isFlagged(), flagged);

        // Change the flag
        assertDoesNotThrow(()->{
            notifTest.changeFlag();
        });

        // Test if the flag in changed in local
        assertEquals(notifTest.isFlagged(), (!flagged));

        // Fetch the notification again to check if the flag changed in the database
        Notification notifTest2 = Notification.fetchCustomNotification().get(0);
        assertEquals(notifTest2.isFlagged(), (!flagged));

        // Flag it again to make it as the start of the test
        assertDoesNotThrow(()->{
            notifTest.changeFlag();
        });
        assertEquals(notifTest.isFlagged(), flagged);
    }

    @Test
    @DisplayName("Toggle an account")
    public void toggleAccount() {
        // Creates a portfolio
        Main.setUser(new Profile("01.02.03-123.00"));
        assertDoesNotThrow(() -> {
            Main.updatePortfolio();
        });
        Account accountTest = Main.getPortfolio().getWalletList().get(0).getAccountList().get(0);
        boolean activated = accountTest.isActivated();
        assertEquals(accountTest.isActivated(), activated);

        // Toggles the account
        assertDoesNotThrow(() -> {
            accountTest.toggle();
        });

        assertEquals(accountTest.isActivated(), (!activated));


        // Toggle the account to make it as the start of the test
        assertDoesNotThrow(() -> {
            accountTest.toggle();
        });

        assertEquals(accountTest.isActivated(), activated);
    }

    @Test
    @DisplayName("Manage expired token")
    public void errorHandler(){
        // Sets an expired token
        String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwMS4wMi4wMy0xMjMuMDAiLCJyb2xlIjoiUk9MRV9VU0VSIiwiaXNzIjoiaHR0cHM6Ly9mbG5zLXNwcmluZy10ZXN0Lmhlcm9rdWFwcC5jb20vYXBpL2xvZ2luIiwiZXhwIjoxNjUwNjI4NjY5fQ.nwtdvJUpnXyhKDZxFdsiD6-nSQxsZmHN5HgpwIEk6b0";
        Main.setToken(expiredToken);

        // Test a simple request
        assertDoesNotThrow(()->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/01.02.03-123.00?isUsername=false")
                    .header("Authorization", "Bearer " + Main.getToken())
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
                    rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/01.02.03-123.00?isUsername=false")
                            .header("Authorization", "Bearer " + Main.getToken())
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
    @DisplayName("Get archived accounts")
    public void fetchArchivedAccounts(){
        Main.setUser(new Profile("01.02.03-123.00"));
        Wallet testWallet = new Wallet(new Profile("01.02.03-123.00"), new Bank("ABCDABCD"), new ArrayList<Account>());
        assertDoesNotThrow(()->{
            testWallet.fetchArchivedAccount();
        });
        ArrayList<Account> archivedAccountList = testWallet.getArchivedAccountList();
        assertEquals(archivedAccountList.size(), 1);
        assertEquals(archivedAccountList.get(0).getIBAN(), "BE00000000000071");
        assertEquals(archivedAccountList.get(0).isArchived(), true);
    }

    @Test
    @DisplayName("Fetch requests")
    public void fetchRequests(){
        assertDoesNotThrow(() -> {
            ArrayList<Request> reqList = Request.fetchRequests();
            assertEquals(reqList.size(), 1);
            assertEquals(reqList.get(0).getCommunicationType(), CommunicationType.CREATE_ACCOUNT);
            assertEquals(reqList.get(0).getDate(), "2022-04-22");
            assertEquals(reqList.get(0).getContent(), "");
            assertEquals(reqList.get(0).getRecipientId(), "GEBABEBB");
        });
    }
//
//    @Test
//    @DisplayName("Fetch all swifts")
//    public void
}
