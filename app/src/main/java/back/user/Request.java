package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Request extends Communication {
    private CommunicationType communicationType;
    private String recipientId;
    private String date;
    private String content;
    private String senderID;
    private long ID;

    public Request(String swift, CommunicationType communicationType, String date, String content, String senderID, long ID) {
        this.recipientId = swift;
        this.communicationType = communicationType;
        this.date = date;
        this.content = content;
        this.senderID = senderID;
        this.ID = ID;
    }

    public void approve() {
        if (this.communicationType.equals(CommunicationType.TRANSFER_PERMISSION)) {
            // Changes the account
            HttpResponse<String> response = null;
            Unirest.setTimeouts(0, 0);
            response = null;
            try {
                response = Unirest.put("https://flns-spring-test.herokuapp.com/api/account")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"iban\": \"" + this.content + "\",\r\n    \"payment\": " + "true" + "\r\n}")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            // Send a notification to the client
            Notification notif = new Notification(Main.getBank().getName(), this.senderID, "The bank " + Main.getBank().getName() + " has given you the transfer permissions for the account " + this.content);
            notif.send();

            // Delete this request
            Unirest.setTimeouts(0, 0);
            try {
                HttpResponse<String> response2 = Unirest.delete("https://flns-spring-test.herokuapp.com/api/notification/" + this.ID)
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }


    }

    public void deny() {
        if (this.communicationType.equals(CommunicationType.TRANSFER_PERMISSION)) {
            // Send a notification to the client
            Notification notif = new Notification(Main.getBank().getName(), this.senderID, "The bank " + Main.getBank().getName() + " hasn't given you the transfer permissions for the account " + this.content);
            notif.send();

            // Delete this request
            Unirest.setTimeouts(0, 0);
            try {
                HttpResponse<String> response2 = Unirest.delete("https://flns-spring-test.herokuapp.com/api/notification/" + this.ID)
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete() {

    }

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
}