package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author François VION
 */
public class Bank {
    private final String name;
    private final String swiftCode;

    /**
     * Creates a Bank object with an HTTP request by using the swift code
     *
     * @param swiftCode A String of the swift code of the bank
     */
    public Bank(String swiftCode) {
        String token = Main.getToken();
        this.swiftCode = swiftCode;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/" + swiftCode)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        Main.errorCheck(response.getStatus());
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.name = obj.getString("name");
    }

    /**
     * Creates a Bank object with all the needed informations
     *
     * @param swiftCode A String of the swift code of the bank
     * @param name      A String of the name of the Bank
     */
    public Bank(String swiftCode, String name) {
        this.swiftCode = swiftCode;
        this.name = name;
    }

    /**
     * Fetch all bank's swift code
     *
     * @return An arraylist of all swift codes
     */
    public static ArrayList<String> fetchAllSWIFT() {
        ArrayList<String> rep = new ArrayList<>();

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep2 = null;
            try {
                rep2 = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep2;
        });

        Main.errorCheck(response.getStatus());
        String body = response.getBody();
        body = body.substring(1, body.length() - 1);
        // Parse all the banks and creates the objetcs
        ArrayList<String> bankList = Portfolio.JSONArrayParser(body);
        for (String s : bankList) {
            JSONObject obj = new JSONObject(s);
            rep.add(obj.getString("swift"));
        }
        return rep;
    }

    public String getName() {
        return this.name;
    }

    public String getSwiftCode() {
        return this.swiftCode;
    }
}
