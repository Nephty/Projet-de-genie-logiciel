package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Account {
    private Profile accountOwner;
    private ArrayList<Profile> accountCoOwner;
    private ArrayList<Transaction> transactionHistory;
    private String IBAN;
    private AccountType accountType;
    private boolean activated;
    private boolean archived;
    private boolean canPay;
    private Bank bank;
    private ArrayList<SubAccount> subAccountList;

    public Account(String IBAN) throws UnirestException {
        this.IBAN = IBAN;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account/" + IBAN)
                .header("Authorization", "Bearer "+ Main.getToken())
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.accountOwner = new Profile(obj.getJSONObject("userId").getString("userID"));
        // TODO : Requête Account access pour les autres valeurs
        int accountTypeId = obj.getJSONObject("accountTypeId").getInt("accountTypeId");
        switch (accountTypeId){
            case 0:
                this.accountType = AccountType.COURANT; break;
            case 1:
                this.accountType = AccountType.JEUNE; break;
            case 2:
                this.accountType = AccountType.EPARGNE; break;
            case 3:
                this.accountType = AccountType.TERME; break;
        }
        this.bank = new Bank(obj.getJSONObject("swift").getString("swift"));
        this.canPay = obj.getBoolean("payment");
    }

    @Override
    public String toString() {
        return "Name : " + IBAN + "           Status : " + (activated ? "activated" : "deactivated");
    }

    /**
     * Return whether the account is toggled on or not.
     * @return Whether the account is toggled on or not
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Toggles the account on.
     */
    public void toggleOn() {
        // TODO : back-end : toggle product on in database
        activated = true;
    }

    /**
     * Toggles the account off.
     */
    public void toggleOff() {
        // TODO : back-end : toggle product off in database
        activated = false;
    }


    public void exportHistory(){
        // TODO : Expoter l'historique
    }

    public Profile getAccountOwner(){
        return this.accountOwner;
    }

    public AccountType getAccountType(){
        return this.accountType;
    }

    public ArrayList<Profile> getAccountCoOwner(){
        return this.accountCoOwner;
    }

    public ArrayList<Transaction> getTransactionHistory(){
        return this.transactionHistory;
    }

    public String getIBAN(){
        return this.IBAN;
    }

    public boolean isArchived(){
        return this.archived;
    }

    public Bank getBank(){
        return this.bank;
    }

    public ArrayList<SubAccount> getSubAccountList(){
        return this.subAccountList;
    }
}
