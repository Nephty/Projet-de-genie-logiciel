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

    private static final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXRhbkBoZWxsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ4MjQxNTIxfQ.Hr0KX07H5BBM9-rI94BmLFMfHK4jdVFfxgM3KG0vOjQ";

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
//            assertEquals("fake8", portfolioTest.getWalletList().get(0).getAccountList().get(0).getIBAN()); // TODO : A voir avec la bdd actuelle
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
        assertDoesNotThrow(() ->{
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/user")
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"username\": \"" + "elonmus" + "\",\r\n    \"userID\": \"" + "01.01.1-001.03" + "\",\r\n    \"email\": \"" + "elon.musketat@tesla.com" + "\",\r\n    \"password\": \"" + "igotalotofmoney" + "\",\r\n    \"firstname\": \"" + "Elon" + "\",\r\n    \"lastname\": \"" + "Musk" + "\",\r\n    \"language\": \"" + "EN US" + "\"\r\n}")
                    .asString();

            System.out.println(response.getBody());
            assertEquals(201, response.getStatus());
        });
    }
}
