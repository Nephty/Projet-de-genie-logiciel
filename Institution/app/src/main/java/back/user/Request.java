package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.scenes.Scenes;
import org.json.JSONObject;

import java.util.ArrayList;

public class Request extends Communication {
    private CommunicationType communicationType;
    private String recipientId;
    private String date;
    private String content;
    private String senderID;
    private long ID;

    /**
     * Creates a request with all the needed informations
     * @param swift             The String of the bank's swift
     * @param communicationType The communication type (enumeration)
     * @param date              The String of the date
     * @param content           The String of the content
     * @param senderID          The String of the sender id
     * @param ID                The id of the request (long)
     */
    public Request(String swift, CommunicationType communicationType, String date, String content, String senderID, long ID) {
        this.recipientId = swift;
        this.communicationType = communicationType;
        this.date = date;
        this.content = content;
        this.senderID = senderID;
        this.ID = ID;
    }


    /**
     * Fetch specific requests in the database
     * @param notificationType  The notification type that will be fetch
     * @return                  The list of specific request
     */
    public static ArrayList<Request> fetchRequests(int notificationType){
        // Fetch requests and put them in the list
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

        ArrayList<Request> reqList = parseRequest(response.getBody(), notificationType);

        return reqList;
    }

    /**
     * Parse a list of JSON into a specific request list
     * @param json              The JSON to parse
     * @param notificationType  The specific notification type (enumeration)
     * @return                  The lis of specific request from JSON
     */
    public static ArrayList<Request> parseRequest(String json, int notificationType){
        ArrayList<Request> rep = new ArrayList<Request>();
        String body = json;
        String toParse = body.substring(1,body.length() - 1);
        ArrayList<String> requestList = Bank.JSONArrayParser(toParse);
        if(!requestList.get(0).equals("")) {
            for (String s : requestList) {
                JSONObject obj = new JSONObject(s);
                // Ignore custom notifications
                if (obj.getInt("notificationType") == notificationType) {
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
                    rep.add(new Request(obj.getString("recipientId"), comType, obj.getString("date"), obj.getString("comments"), obj.getString("senderId"), obj.getLong("notificationId")));
                }
            }
        }
        return rep;
    }


    /**
     * Approves the request
     */
    public void approve() {
        if (this.communicationType.equals(CommunicationType.TRANSFER_PERMISSION)) {
            // Changes the account
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.put("https://flns-spring-test.herokuapp.com/api/account")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .header("Content-Type", "application/json")
                            .body("{\r\n    \"iban\": \"" + this.content + "\",\r\n    \"payment\": " + "true" + "\r\n}")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });

            sendNotif("has given you the transfer permissions for the account " + this.content);
        }

        if(this.communicationType.equals(CommunicationType.CREATE_ACCOUNT)){
            // Creates an account
            Main.setNewClient(this.senderID);
            Main.setRequest(this);
            Main.setScene(Flow.forward(Scenes.CreateClientAccountScene));
        }

        if(this.communicationType.equals(CommunicationType.DELETE_ACCOUNT)){
            // Delete the account
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.delete("https://flns-spring-test.herokuapp.com/api/account/" + this.content)
                            .header("Authorization", "Bearer " + Main.getToken())
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });

            sendNotif("has deleted the account "+this.content);
        }
    }

    /**
     * Deny the request
     */
    public void deny() {
        if (this.communicationType.equals(CommunicationType.TRANSFER_PERMISSION)) {
            // Send a notification to the client
            sendNotif("hasn't given you the transfer permission for your account +" + this.content);
        }

        if(this.communicationType.equals(CommunicationType.CREATE_ACCOUNT)){
            // Send a notification to the client
            sendNotif("hasn't created you a new account");
        }
        if(this.communicationType.equals(CommunicationType.DELETE_ACCOUNT)){
            // Send a notification to the client
            sendNotif("hasn't deleted your account " + this.content);
        }
    }

    /**
     * Send a notification to the client
     * @param message   The message to put in the notification's content
     */
    public void sendNotif(String message){
        Notification notif = new Notification(Main.getBank().getName(), this.senderID, "The bank " + Main.getBank().getName() + " " + message);
        notif.send();

        // Delete this request
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.delete("https://flns-spring-test.herokuapp.com/api/notification/" + this.ID)
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
    }

    /**
     * @return A String to display the request
     */
    @Override
    public String toString() {
        String comType = "";
        switch (this.communicationType.toString()) {
            case ("CREATE_ACCOUNT"):
                comType = "Create account";
                break;
            case ("CREATE_SUB_ACCOUNT"):
                comType = "Create account";
                break;
            case ("TRANSFER_PERMISSION"):
                comType = "Transfer permission";
                break;
            case ("NEW_WALLET"):
                comType = "New wallet";
                break;
            case ("DELETE_ACCOUNT"):
                comType = "Delete account";
                break;
        }
        return this.date + "      " + this.recipientId + "      " + comType;
    }

    public CommunicationType getReason() {
        return this.communicationType;
    }

    public String getDate() {
        return date;
    }

    public String getSenderID() {
        return senderID;
    }

    public long getID() {
        return ID;
    }

    public String getContent(){
        return this.content;
    }
}