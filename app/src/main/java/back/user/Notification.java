package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Notification extends Communication {
    private final String content;
    private String senderName;
    private String recipientId;

    public Notification(String senderName, String recipientId, String content) {
        this.content = content;
        this.senderName = senderName;
        this.recipientId = recipientId;
    }

    public void send() {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("https://flns-spring-test.herokuapp.com/api/notification")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"notificationType\": 4,\r\n    \"comments\": \"" + this.content + "\",\r\n    \"recipientId\": \"" + this.recipientId + "\"\r\n}")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    public String getContent() {
        return this.content;
    }

    public String getSenderName() {
        return this.senderName;
    }
}