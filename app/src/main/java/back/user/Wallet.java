package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

import static back.user.Portfolio.JSONArrayParser;

public class Wallet {
    private final ArrayList<Account> accountList;
    private final Profile accountUser;
    private final Bank bank;
    private int numberOfAccounts = 0;

    private ArrayList<Account> archivedAccountList;

    /**
     * Creates a Wallet object with all the needed information
     *
     * @param accountUser The Profile object of the user
     * @param bank        The Bank object of the bank
     * @param accountList The list of account
     * @throws UnirestException
     */
    public Wallet(Profile accountUser, Bank bank, ArrayList<Account> accountList) throws UnirestException {
        this.accountUser = accountUser;
        this.bank = bank;
        this.accountList = accountList;
        this.numberOfAccounts = accountList.size();
    }

    public Wallet(Profile accountUser, Bank bank){
        this.accountUser = accountUser;
        this.bank = bank;
        this.accountList = new ArrayList<Account>();
    }

    public void fetchArchivedAccount() {

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
        this.archivedAccountList = (createsAccountList(response2.getBody()));


        // Fetch hidden account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response3;
        try {
            response3 = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+this.accountUser.getNationalRegistrationNumber()+"&hidden=true&deleted=false")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        this.archivedAccountList.addAll(createsAccountList(response3.getBody()));
    }


    public ArrayList<Account> createsAccountList(String body){
        ArrayList<Account> accountListRep = new ArrayList<Account>();

        body = body.substring(1, body.length() - 1);
        ArrayList<String> bodyList = Portfolio.JSONArrayParser(body);
        if(!body.equals("")) {
            // Creates the accounts
            for (int i = 0; i < bodyList.size(); i++) {
                JSONObject obj = new JSONObject(bodyList.get(i));
                String swift = obj.getString("accountId");
                Profile owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), obj.getJSONObject("account").getString("ownerLastname"), obj.getString("userId"));
                // TODO : Réparer
                // Profile coOwner = new Profile(obj.getJSONObject("userId").getString("firstname"), obj.getJSONObject("userId").getString("lastname"), obj.getJSONObject("userId").getString("userID"));
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
                boolean activated = obj.getBoolean("hidden");
                boolean archived = obj.getJSONObject("account").getBoolean("deleted");
                boolean canPay = obj.getJSONObject("account").getBoolean("payment");
                // TODO : Remettre coOwner

                accountListRep.add(new Account(owner, owner, this.bank, iban, accountType, activated, archived, canPay));
            }
        }
        return accountListRep;
    }


    /**
     * @return A String to display Wallet information
     */
    @Override
    public String toString() {
        String espace = "                    ";
        espace.substring(0, espace.length() - this.bank.getName().length());

        return "Bank : " + this.bank.getName() + espace + "SWIFT : " + this.bank.getSwiftCode();
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

    public int getNumberOfAccounts() {
        this.numberOfAccounts = accountList.size();
        return numberOfAccounts;
    }

    public ArrayList<Account> getArchivedAccountList(){
        return this.archivedAccountList;
    }
}
