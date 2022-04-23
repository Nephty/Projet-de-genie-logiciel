package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Wallet {
    private Profile accountUser;
    private ArrayList<Account> accountList;
    private Bank bank;

    /**
     * Creates a Wallet object with the account user
     *
     * @param accountUser The Profile object of the user
     */
    public Wallet(Profile accountUser){
        this.accountUser = accountUser;
        this.bank = Main.getBank();
        update();
    }

    /**
     * Creates a Wallet with all the needed information
     *
     * @param accountUser
     * @param accountList
     */
    public Wallet(Profile accountUser, ArrayList<Account> accountList){
        this.accountUser = accountUser;
        this.bank = Main.getBank();
        this.accountList = accountList;
    }

    /**
     * Update the wallet
     */
    public void update(){
        // Fetch all active client's account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId=" + this.accountUser.getNationalRegistrationNumber() + "&hidden=false&deleted=false")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        this.accountList = createsAccountList(response.getBody());

        // Fetch deleted account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response2;
        try {
            response2 = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+this.accountUser.getNationalRegistrationNumber()+"&hidden=false&deleted=true")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        this.accountList.addAll(createsAccountList(response2.getBody()));


        // Fetch hidden account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response3 = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+this.accountUser.getNationalRegistrationNumber()+"&hidden=true&deleted=false")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });


        this.accountList.addAll(createsAccountList(response3.getBody()));

    }

    /**
     * Creates the list of account with a JSON
     * @param body  The String of the JSON
     * @return      The account list of the JSON
     */
    public static ArrayList<Account> createsAccountList(String body){
        ArrayList<Account> accountListRep = new ArrayList<Account>();

        body = body.substring(1, body.length() - 1);
        // Parse the Strings
        ArrayList<String> bodyList = Bank.JSONArrayParser(body);
        // If there is at least one account
        if(!body.equals("")) {
            // Creates the accounts and add them to the list
            for (int i = 0; i < bodyList.size(); i++) {
                JSONObject obj = new JSONObject(bodyList.get(i));
                String swift = obj.getJSONObject("account").getString("swift");
                // If the account is in this bank
                if(swift.equals(Main.getBank().getSwiftCode())){
                    Profile owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), obj.getJSONObject("account").getString("ownerLastname"), obj.getJSONObject("account").getString("userId"));
                    Profile coOwner = new Profile(obj.getString("userId"));
                    String iban = obj.getString("accountId");
                    int accountTypeId = obj.getJSONObject("account").getInt("accountTypeId");
                    AccountType accountType = null;
                    switch (accountTypeId) {
                        case 1:
                            accountType = AccountType.COURANT;
                            break;
                        case 2:
                            accountType = AccountType.JEUNE;
                            break;
                        case 3:
                            accountType = AccountType.EPARGNE;
                            break;
                        case 4:
                            accountType = AccountType.TERME;
                            break;
                    }
                    boolean activated = (!obj.getBoolean("hidden"));
                    boolean archived = obj.getJSONObject("account").getBoolean("deleted");
                    boolean canPay = obj.getJSONObject("account").getBoolean("payment");

                    accountListRep.add(new Account(owner, coOwner, Main.getBank(), iban, accountType, activated, archived, canPay));
                }
            }
        }
        return accountListRep;
    }

    /**
     * Deletes all the accounts in a wallet (which delete the client)
     */
    public void deleteAll(){
        for(int i = 0; i<this.accountList.size(); i++){
            this.accountList.get(i).delete();
        }
    }

    public ArrayList<Account> getAccountList() {
        return this.accountList;
    }

    public Profile getAccountUser() {
        return this.accountUser;
    }

    public Bank getBank() {
        return this.bank;
    }
}
