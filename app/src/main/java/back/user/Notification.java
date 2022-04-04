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

    public Notification(String senderName, String content, String date, long ID) {
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
        // TODO : DELETE Notification
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
        // TODO : PUT Notification
        if(flag){
            this.flag = false;
        } else{
            this.flag = true;
        }
    }
}
