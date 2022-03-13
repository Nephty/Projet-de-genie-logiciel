package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class Bank {
    private String name;
    private String swiftCode;

    public Bank(String swiftCode){
        this.swiftCode = swiftCode;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank")
                    .header("Authorization", "Bearer "+ Main.getToken())
                    .header("Content-Type", "application/json")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace(); // TODO : Meilleur try catch
        }
        String body = response.getBody();
        body = body.substring(1 , body.length() - 1);
        JSONObject obj = new JSONObject(body);
        this.name = obj.getString("name");
    }

    public String getName(){
        return this.name;
    }

    public String getSwiftCode(){
        return this.swiftCode;
    }
}
