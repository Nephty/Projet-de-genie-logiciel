package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Notification extends Communication {
    private final String content;
    private String senderName;
    private String date;
    private boolean flag;
    private long ID;

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

    public String getSenderName() {
        return this.senderName;
    }

    public void dismiss() {
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.delete("https://flns-spring-test.herokuapp.com/api/notification/" + this.ID)
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void changeFlag() {
        this.flag = !flag;

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.put("https://flns-spring-test.herokuapp.com/api/notification")
                    .header("Authorization", "Bearer "+ Main.getToken())
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"notificationId\": "+this.ID+",\r\n    \"isFlagged\": "+this.flag+"\r\n}")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
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
