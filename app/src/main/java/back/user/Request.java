package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Request extends Communication {
    private CommunicationType communicationType;
    private String recipientId;


    public Request(String swift, CommunicationType communicationType) {
        this.recipientId = swift;
        this.communicationType = communicationType;
        this.wallet = null;
    }

    public Request(Wallet wallet, CommunicationType communicationType) {
        this.wallet = wallet;
        this.communicationType = communicationType;
        this.bank = null;
        this.recipientId = null;
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

    public CommunicationType getReason() {
        return this.communicationType;
    }
}