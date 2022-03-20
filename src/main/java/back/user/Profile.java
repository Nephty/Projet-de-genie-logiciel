package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Profile {
    private final String firstName;
    private final String lastName;
    private final String nationalRegistrationNumber;

    public Profile(String nationalRegistrationNumber) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber +"?isUsername=false")
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

//    public void changePassword(String newPassword) throws UnirestException {
//        Unirest.setTimeouts(0, 0);
//        HttpResponse<String> response = null;
//        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber +"?isUsername=false")
//                .header("Authorization", "Bearer " + Main.getToken())
//                .asString();
//        String body = response.getBody();
//        JSONObject obj = new JSONObject(body);
//
//        Unirest.setTimeouts(0, 0);
//        HttpResponse<String> response2 = Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
//                .header("Authorization", "Bearer "+ Main.getToken())
//                .header("Content-Type", "application/json")
//                .body("{\r\n    \"name\": \""+this.firstName+"\",\r\n    \"id\": "+this.nationalRegistrationNumber+",\r\n    \"email\": \""+obj.getString("email")+"\",\r\n    \"password\": \""+newPassword+"\"\r\n}")
//                .asString();
//    }

    public static ArrayList<Profile> fetchAllCustomers(String swift) {
        ArrayList<Profile> rep = new ArrayList<Profile>();
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access?swift=" + swift)
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        String body = response.getBody();
        body = body.substring(1,body.length() -1);
        ArrayList<String> customerList = Bank.JSONArrayParser(body);

        ArrayList<String> userIdList = new ArrayList<String>();

        for(int i = 0; i<customerList.size(); i++){
            JSONObject obj = new JSONObject(customerList.get(i));
            if(!userIdList.contains(obj.getString("userID")) && (!obj.getString("userID").equals("000000000"))){
                rep.add(new Profile(obj.getString("firstname"),obj.getString("lastname"),obj.getString("userID")));
                userIdList.add(obj.getString("userID"));
            }
        }
        return rep;
    }

    @Override
    public String toString(){
        return this.firstName +"   "+this.lastName+ "     "+ this.nationalRegistrationNumber;
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
