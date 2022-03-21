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

    /**
     * Creates a Profile object with an HTTP request by using the user's national registration number
     * @param nationalRegistrationNumber The String of the user's national registration number
     * @throws UnirestException For managing HTTP errors
     */
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


    /**
     * Creates a Profile object by giving all the needed informations
     * @param firstName A string of the firstname
     * @param lastName A string of the lastname
     * @param nationalRegistrationNumber A string of the national registration number
     */
    public Profile(String firstName, String lastName, String nationalRegistrationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
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