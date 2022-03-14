import back.user.Bank;
import back.user.Profile;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTest {

    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXRhbkBoZWxsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ4MjQxNTIxfQ.Hr0KX07H5BBM9-rI94BmLFMfHK4jdVFfxgM3KG0vOjQ";

    @Test
    @DisplayName("Getting a user from api")
    public void getUser() {
        assertDoesNotThrow(() -> {
            Profile userTest = new Profile("123456789");
            assertEquals("Morningstar", userTest.getFirstName());
            assertEquals("Lucifer", userTest.getLastName());
            assertEquals("123456789", userTest.getNationalRegistrationNumber());
        });

    }

    @Test
    @DisplayName("Getting a bank from api")
    public void getBank() throws UnirestException {
        Bank bankTest = new Bank("ABCD");

        assertEquals("Belfius",bankTest.getName());
        assertEquals("ABCD", bankTest.getSwiftCode());
    }
}
