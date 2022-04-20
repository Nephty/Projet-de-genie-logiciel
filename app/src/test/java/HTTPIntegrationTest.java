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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
            assertEquals("Belfius", portfolioTest.getWalletList().get(0).getBank().getName());
//            assertEquals("0123456789ABCDEF", portfolioTest.getWalletList().get(0).getAccountList().get(0).getIBAN());
//            assertEquals(1, portfolioTest.getWalletList().get(0).getAccountList().get(0).getSubAccountList().get(0).getTransactionHistory().get(0).getID());
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
    public void getNotifications(){
        assertDoesNotThrow(() ->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/notification")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });
            ArrayList<Notification> notifList = new ArrayList<Notification>();
            if (response != null) {
                String body = response.getBody();
                String toParse = body.substring(1, body.length() - 1);
                ArrayList<String> notificationList = Portfolio.JSONArrayParser(toParse);
                if (!notificationList.get(0).equals("")) {
                    for (String s : notificationList) {
                        JSONObject obj = new JSONObject(s);
                        if (obj.getInt("notificationType") == 4) {
                            notifList.add(new Notification(obj.getString("senderName"), obj.getString("comments"), obj.getString("date"), obj.getLong("notificationId"), obj.getBoolean("isFlagged")));
                        }
                    }
                }
            }
            assertEquals(notifList.size(), 1);
            assertEquals(notifList.get(0).getID(), 84);
            assertEquals(notifList.get(0).getDate(), "2022-04-20");
            assertEquals(notifList.get(0).getContent(), "The Bank BNP hasn't created you a new account");
        });
    }
}
