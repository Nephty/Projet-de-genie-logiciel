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

    public Bank(String swiftCode) throws UnirestException {
        String token = Main.getToken();
        this.swiftCode = swiftCode;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/" + swiftCode)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.name = obj.getString("name");
    }

    public static ArrayList<String> fetchAllSWIFT() throws UnirestException {
        ArrayList<String> rep = new ArrayList<String>();
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank")
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();
        String body = response.getBody();
        body = body.substring(1, body.length() -1);
        ArrayList<String> bankList = Portfolio.JSONArrayParser(body);
        for(int i = 0; i< bankList.size(); i++){
            JSONObject obj = new JSONObject(bankList.get(i));
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
