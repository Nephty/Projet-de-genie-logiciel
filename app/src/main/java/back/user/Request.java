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