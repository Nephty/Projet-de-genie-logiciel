import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTest {

    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXRhbkBoZWxsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpL2xvZ2luIiwiZXhwIjoxNjQ4MjQxNTIxfQ.Hr0KX07H5BBM9-rI94BmLFMfHK4jdVFfxgM3KG0vOjQ";

    @Test
    @DisplayName("Getting a user from api test")
    public void getUser() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + "123456789")
                .header("Authorization", "Bearer "+ token)
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        System.out.println(body);
        String expected = "{\"userID\":\"123456789\",\"username\":\"satan@hell.com\",\"lastname\":\"Lucifer\",\"firstname\":\"Morningstar\",\"email\":\"Satan\",\"language\":\"FR\"}";
        assertEquals(expected, body);
    }
}
