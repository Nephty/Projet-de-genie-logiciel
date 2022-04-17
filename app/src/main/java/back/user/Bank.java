package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Bank {
    private final String name;
    private final String swiftCode;

    /**
     * Creates a Bank object with an HTTP request by using the swift code
     *
     * @param swiftCode A String of the swift code of the bank
     * @throws UnirestException For managing HTTP errors
     */
    public Bank(String swiftCode){
        String token = Main.getToken();
        this.swiftCode = swiftCode;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/" + swiftCode)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
        Main.errorCheck(response.getStatus());
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.name = obj.getString("name");
    }

    /**
     * A method for parsing arrays in JSON
     *
     * @param json The String to parse
     * @return A list of parsed Strings
     */
    public static ArrayList<String> JSONArrayParser(String json) {
        ArrayList<String> rep = new ArrayList<String>();
        int crochet = 0;
        int save = 0;
        for (int i = 0; i < json.length(); i++) {
            if (json.charAt(i) == '{') {
                crochet++;
            }
            if (json.charAt(i) == '}') {
                crochet--;
            }
            if (json.charAt(i) == ',' && crochet == 0) {
                rep.add(json.substring(save, i));
                save = i + 1;
            }
        }
        rep.add(json.substring(save));
        return rep;
    }

    public String getName() {
        return this.name;
    }

    public String getSwiftCode() {
        return this.swiftCode;
    }

    @Override
    public String toString() {
        return name;
    }
}
