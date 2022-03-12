package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class Profile {
    private String firstName;
    private String lastName;
    private String nationalRegistrationNumber;
    private Portfolio portfolio;

    public Profile(String nationalRegistrationNumber) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber)
                .header("Authorization", "Bearer "+ Main.getToken())
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.firstName = obj.getString("firstname");
        this.lastName = obj.getString("lastname");
        this.nationalRegistrationNumber = nationalRegistrationNumber;
        // TODO : Faire la requête dans Portfolio pour créer  le portfolio a partir du nrn
//        this.portfolio = new Portfolio(nationalRegistrationNumber);
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getNationalRegistrationNumber(){
        return this.nationalRegistrationNumber;
    }

    public Portfolio getPortfolio(){
        return this.portfolio;
    }
}
