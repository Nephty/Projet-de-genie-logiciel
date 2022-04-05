package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Request extends Communication {
    private CommunicationType communicationType;
    private String recipientId;
    private String date;


    public Request(String swift, CommunicationType communicationType, String date) {
        this.recipientId = swift;
        this.communicationType = communicationType;
        this.date = date;
    }

    public void send() {
        int comType = 6;
        switch(this.communicationType.toString()){
            case("CREATE_ACCOUNT"): comType = 0; break;
            case("CREATE_SUB_ACCOUNT"): comType = 1; break;
            case("TRANSFER_PERMISSION"): comType = 2; break;
            case("NEW_PORTFOLIO"): comType = 3; break;
            case("NEW_WALLET"): comType = 5; break;
        }
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/notification")
                    .header("Authorization", "Bearer "+ Main.getToken())
                    .header("Content-Type", "application/json")
                    .body("{\r\n    \"notificationType\": "+ comType +",\r\n    \"comments\": \" \",\r\n    \"status\": \"Unchecked\",\r\n    \"recipientId\": \""+this.recipientId+"\"\r\n}")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        String comType = "";
        switch(this.communicationType.toString()){
            case("CREATE_ACCOUNT"): comType = "Create account"; break;
            case("CREATE_SUB_ACCOUNT"): comType = "Create account"; break;
            case("TRANSFER_PERMISSION"): comType = "Transfer permission"; break;
            case("NEW_PORTFOLIO"): comType = "New portfolio"; break;
            case("NEW_WALLET"): comType = "New wallet"; break;
        }
        return this.date + "      "+ this.recipientId + "      " + comType + "         waiting for the bank's approvment";
    }

    public CommunicationType getReason() {
        return this.communicationType;
    }
}