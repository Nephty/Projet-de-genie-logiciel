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

    public static HttpResponse<String> handlePossibleError(Supplier<HttpResponse<String>> toRetry) {
        HttpResponse<String> response = toRetry.get();

        if(response.getStatus() == 412) {
            refreshToken();
            return handlePossibleError(toRetry);
        }

        return response;
    }


    public static void refreshToken() {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/token/refresh")
                    .header("Authorization", "Bearer " + Main.getRefreshToken())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        Main.setToken(obj.getString("access_token"));
        Main.setRefreshToken(obj.getString("refresh_token"));
    }
}