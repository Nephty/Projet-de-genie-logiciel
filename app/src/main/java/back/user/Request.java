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
public class Request extends Communication {
    private final CommunicationType communicationType;
    private final String recipientId;
    private final String date;
    private final String content;


    /**
     * Creates a request with all the needed information
     * @param swift             The String of the bank's swift
     * @param communicationType The communication type (enumeration)
     * @param date              The String of the date
     * @param content           The String of the content
     */
    public Request(String swift, CommunicationType communicationType, String date, String content) {
        this.recipientId = swift;
        this.communicationType = communicationType;
        this.date = date;
        this.content = content;
    }

    /**
     * Send the request
     */
    public void send() {
        boolean alreadySent = false;

        // Fetch all the requests to check if the user already sent the same request
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/notification")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        if (response != null) {
            String body = response.getBody();
            String toParse = body.substring(1,body.length() - 1);
            ArrayList<String> requestList = Portfolio.JSONArrayParser(toParse);
            if(!requestList.get(0).equals("")) {
                for (String s : requestList) {
                    JSONObject obj = new JSONObject(s);
                    // We exclude custom notification and alert
                    if (obj.getInt("notificationType") != 4 && obj.getInt("notificationType") != 5) {
                        CommunicationType comType = CommunicationType.CUSTOM;
                        int notifType = obj.getInt("notificationType");
                        switch (notifType) {
                            case (0):
                                comType = CommunicationType.CREATE_ACCOUNT;
                                break;
                            case (1):
                                comType = CommunicationType.CREATE_SUB_ACCOUNT;
                                break;
                            case (2):
                                comType = CommunicationType.TRANSFER_PERMISSION;
                                break;
                            case (3):
                                comType = CommunicationType.NEW_WALLET;
                                break;
                            case (6):
                                comType = CommunicationType.DELETE_ACCOUNT;
                                break;
                        }
                        // If a request has the same recipient, communication type and content, the request has already been send
                        if (this.recipientId.equals(obj.getString("recipientId")) && this.communicationType == comType && this.content.equals(obj.getString("comments"))) {
                            alreadySent = true;
                        }
                    }
                }
            }
        }
        // TODO: Ecrire un truc pour l'utilisateur
        if(!alreadySent){
            int comType = 7;
            switch(this.communicationType.toString()){
                case("CREATE_ACCOUNT"): comType = 0; break;
                case("CREATE_SUB_ACCOUNT"): comType = 1; break;
                case("TRANSFER_PERMISSION"): comType = 2; break;
                case("NEW_WALLET"): comType = 3; break;
                case("DELETE_ACCOUNT"): comType = 6; break;
            }

            // Send the request (creating a notification in the database)
            Unirest.setTimeouts(0, 0);
            int finalComType = comType;
            HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/notification")
                            .header("Authorization", "Bearer "+ Main.getToken())
                            .header("Content-Type", "application/json")
                            .body("{\r\n    \"notificationType\": "+ finalComType +",\r\n    \"comments\": \""+this.content+"\",\r\n    \"status\": \"Unchecked\",\r\n    \"recipientId\": \""+this.recipientId+"\"\r\n}")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });
        }
    }

    /**
     * @return The String to display the request information
     */
    @Override
    public String toString(){
        String comType = "";
        switch(this.communicationType.toString()){
            case("CREATE_ACCOUNT"): comType = "Create account"; break;
            case("CREATE_SUB_ACCOUNT"): comType = "Create sub account"; break;
            case("TRANSFER_PERMISSION"): comType = "Transfer permission"; break;
            case("NEW_WALLET"): comType = "New wallet"; break;
            case("DELETE_ACCOUNT"): comType = "Delete account"; break;
        }
        return this.date + "      "+ this.recipientId + "      " + comType + "         waiting for the bank's approbation";
    }

    public String getDate() {
        return date;
    }

}