import app.Main;
import back.user.Bank;
import back.user.Portfolio;
import back.user.Profile;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTest {

    private static final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYXRhbiIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ5NjE1MTEyLCJ1c2VySWQiOiIxMjM0NTY3ODkifQ.5LP2W6CDGPCgjnbTlQZNv18u7JZtgcU4pjpu6xMooJA";

    @Test
    @DisplayName("Getting a user from api")
    public void getUser() {
        Main.setToken(token);
        assertDoesNotThrow(() -> {
            Profile userTest = new Profile("123456789");

            assertEquals("Lucifer", userTest.getFirstName());
            assertEquals("Morningstar", userTest.getLastName());
            assertEquals("123456789", userTest.getNationalRegistrationNumber());
        });

    }

    @Test
    @DisplayName("Getting a bank from api")
    public void getBank() {
        Main.setToken(token);
        assertDoesNotThrow(() -> {
            Bank bankTest = new Bank("ABCD");

            assertEquals("Belfius", bankTest.getName());
            assertEquals("ABCD", bankTest.getSwiftCode());
        });
    }

    @Test
    @DisplayName("Getting a portfolio from api")
    public void getPortfolio() {
        Main.setToken(token);
        assertDoesNotThrow(() -> {
            Portfolio portfolioTest = new Portfolio("123456789");

            assertEquals("Lucifer", portfolioTest.getUser().getFirstName());
            assertEquals("Morningstar", portfolioTest.getUser().getLastName());
            assertEquals("123456789", portfolioTest.getUser().getNationalRegistrationNumber());
            assertEquals("Belfius", portfolioTest.getWalletList().get(0).getBank().getName());
            assertEquals("uwu69420", portfolioTest.getWalletList().get(0).getAccountList().get(0).getIBAN()); // TODO : A voir avec la bdd actuelle
        });
    }

    @Test
    @DisplayName("Login to an account")
    public void login() {
        assertDoesNotThrow(() -> {
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", "Satan")
                    .field("password", "666HELL")
                    .field("role", "ROLE_USER")
                    .asString();

            System.out.println(response.getBody());
            assertEquals(200, response.getStatus());
        });
    }

    @Test
    @DisplayName("Register a account")
    public void register(){
        Main.setToken(token);
        assertDoesNotThrow(() ->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/user")
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"username\": \"" + "elonm" + "\",\r\n    \"userID\": \"" + "01.01.1-001.01" + "\",\r\n    \"email\": \"" + "elon.musk@tesla.com" + "\",\r\n    \"password\": \"" + "igotalotofmoney" + "\",\r\n    \"firstname\": \"" + "Elon" + "\",\r\n    \"lastname\": \"" + "Musk" + "\",\r\n    \"language\": \"" + "EN US" + "\"\r\n}")
                    .asString();

            assertEquals(201, response.getStatus());
        });

        //Delete the user to finish the test
        assertDoesNotThrow(() ->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.delete("https://flns-spring-test.herokuapp.com/api/user/01.01.1-001.01")
                    .header("Authorization", "Bearer " + token)
                    .asString();
            assertEquals(200, response.getStatus());
        });

    }
}
