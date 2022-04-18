package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author François VION
 */
public class Notification extends Communication {
    private final String content;
    private final String senderName;
    private final  String date;
    private boolean flag;
    private final long ID;

    public Notification(String senderName, String content, String date, long ID, boolean flag) {
        this.content = content;
        this.senderName = senderName;
        this.date = date;
        this.ID = ID;
        this.flag = flag;
    }

    @Override
    public String toString(){
        return this.date + "      " + this.senderName + "     " + this.content;
    }

    public String getContent() {
        return this.content;
    }

    public void dismiss() {
        Unirest.setTimeouts(0, 0);
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

    public void changeFlag() {
        this.flag = !flag;

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.put("https://flns-spring-test.herokuapp.com/api/notification")
                        .header("Authorization", "Bearer "+ Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"notificationId\": "+this.ID+",\r\n    \"isFlagged\": "+this.flag+"\r\n}")
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
    }

    public Long getID() {
        return ID;
    }

    public String getDate() {
        return date;
    }

    public boolean isFlagged() {
        return flag;
    }
}
