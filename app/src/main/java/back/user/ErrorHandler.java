package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.function.Supplier;

/**
 * @author François VION
 */
public class ErrorHandler {

    /**
     * Manage the expired token error
     * @param toRetry The HTTP request to manage
     * @return The response of the HTTP request
     */
    public static HttpResponse<String> handlePossibleError(Supplier<HttpResponse<String>> toRetry) {
        HttpResponse<String> response = toRetry.get();

        // If the token is expired (error 412), it refresh the token and make again the HTTP request
        if(response.getStatus() == 412) {
            refreshToken();
            return handlePossibleError(toRetry);
        }

        Main.errorCheck(response.getStatus());
        return response;
    }

    /**
     * Refresh the token
     */
    public static void refreshToken() {
        // Make an HTTP request for getting a new token with the actual refresh token
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/token/refresh")
                    .header("Authorization", "Bearer " + Main.getRefreshToken())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        // Get the tokens and put it in the variables in Main class
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        Main.setToken(obj.getString("access_token"));
        Main.setRefreshToken(obj.getString("refresh_token"));
    }
}