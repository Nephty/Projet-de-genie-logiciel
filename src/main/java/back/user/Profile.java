package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class Profile {
    private final String firstName;
    private final String lastName;
    private final String nationalRegistrationNumber;

    public Profile(String nationalRegistrationNumber) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber + "?isUsername=false")
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.firstName = obj.getString("firstname");
        this.lastName = obj.getString("lastname");
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }

    public Profile(String firstName, String lastName, String nationalRegistrationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }

    public void changePassword(String newPassword) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber + "?isUsername=false")
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response2 = Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
                .header("Authorization", "Bearer " + Main.getToken())
                .header("Content-Type", "application/json")
                .body("{\r\n    \"name\": \"" + this.firstName + "\",\r\n    \"id\": " + this.nationalRegistrationNumber + ",\r\n    \"email\": \"" + obj.getString("email") + "\",\r\n    \"password\": \"" + newPassword + "\"\r\n}")
                .asString();

    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getNationalRegistrationNumber() {
        return this.nationalRegistrationNumber;
    }
}
