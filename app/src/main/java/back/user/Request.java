package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        // TODO : Vérifier si la requête existe pas déjà

        boolean alreadySent = false;

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/notification")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        String body = response.getBody();
        String toParse = body.substring(1,body.length() - 1);
        ArrayList<String> requestList = Portfolio.JSONArrayParser(toParse);
        ArrayList<Request> reqList = new ArrayList<Request>();
        if(!requestList.get(0).equals("")){
            for(int i = 0; i<requestList.size(); i++){
                JSONObject obj = new JSONObject(requestList.get(i));
                if(obj.getInt("notificationType") != 4){
                    CommunicationType comType = CommunicationType.CUSTOM;
                    int notifType = obj.getInt("notificationType");
                    switch(notifType){
                        case(0): comType = CommunicationType.CREATE_ACCOUNT; break;
                        case(1): comType = CommunicationType.CREATE_SUB_ACCOUNT; break;
                        case(2): comType = CommunicationType.TRANSFER_PERMISSION; break;
                        case(3): comType = CommunicationType.NEW_WALLET; break;
                    }
                    if(this.recipientId.equals(obj.getString("recipientId")) && this.communicationType == comType){
                        alreadySent = true;
                    }
                }
            }
        }

        if(!alreadySent){
            int comType = 6;
            switch(this.communicationType.toString()){
                case("CREATE_ACCOUNT"): comType = 0; break;
                case("CREATE_SUB_ACCOUNT"): comType = 1; break;
                case("TRANSFER_PERMISSION"): comType = 2; break;
                case("NEW_WALLET"): comType = 3; break;
            }
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response2 = null;
            try {
                response2 = Unirest.post("https://flns-spring-test.herokuapp.com/api/notification")
                        .header("Authorization", "Bearer "+ Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"notificationType\": "+ comType +",\r\n    \"comments\": \" \",\r\n    \"status\": \"Unchecked\",\r\n    \"recipientId\": \""+this.recipientId+"\"\r\n}")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String toString(){
        String comType = "";
        switch(this.communicationType.toString()){
            case("CREATE_ACCOUNT"): comType = "Create account"; break;
            case("CREATE_SUB_ACCOUNT"): comType = "Create account"; break;
            case("TRANSFER_PERMISSION"): comType = "Transfer permission"; break;
            case("NEW_WALLET"): comType = "New wallet"; break;
        }
        return this.date + "      "+ this.recipientId + "      " + comType + "         waiting for the bank's approvment";
    }

    public CommunicationType getReason() {
        return this.communicationType;
    }
}