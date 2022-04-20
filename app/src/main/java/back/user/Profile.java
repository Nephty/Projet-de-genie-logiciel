package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

/**
 * @author François VION
 */
public class Profile {
    private final String firstName;
    private final String lastName;
    private final String nationalRegistrationNumber;
    private final String username;
    private final String favoriteLanguage;

    /**
     * Creates a Profile object with an HTTP request by using the user's national registration number
     *
     * @param nationalRegistrationNumber The String of the user's national registration number
     */
    public Profile(String nationalRegistrationNumber){
        // Fetch the user's information in the database
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber + "?isUsername=false")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        // Check the HTTP code status to inform the user if there is an error
        Main.errorCheck(response.getStatus());

        // Parse the information and put them in the variables
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.favoriteLanguage = obj.getString("language");
        this.firstName = obj.getString("firstname");
        this.username = obj.getString("username");
        this.lastName = obj.getString("lastname");
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }


    /**
     * Creates a Profile object by giving all the needed information
     *
     * @param firstName                  A string of the firstname
     * @param lastName                   A string of the lastname
     * @param favoriteLanguage           A string of the favorite language (Locale.getDisplayName() format)
     * @param nationalRegistrationNumber A string of the national registration number
     */
    public Profile(String firstName, String lastName, String username, String favoriteLanguage, String nationalRegistrationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.favoriteLanguage = favoriteLanguage;
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

    public String getFavoriteLanguage() {
        return favoriteLanguage;
    }

    public String getUsername() {
        return username;
    }
}
