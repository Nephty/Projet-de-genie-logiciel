package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Notification extends Communication {
    private final String content;
    private String senderName;
    private String recipientId;

    /**
     * Creates a notification with all the needed informations
     * @param senderName    The String of the sender name
     * @param recipientId   The String of the recipient id
     * @param content       The String of the content
     */
    public Notification(String senderName, String recipientId, String content) {
        this.content = content;
        this.senderName = senderName;
        this.recipientId = recipientId;
    }

    /**
     * Send the notification in the database
     */
    public void send() {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/notification")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"notificationType\": 4,\r\n    \"comments\": \"" + this.content + "\",\r\n    \"recipientId\": \"" + this.recipientId + "\"\r\n}")
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
    }


    public String getContent() {
        return this.content;
    }

    public String getSenderName() {
        return this.senderName;
    }
}