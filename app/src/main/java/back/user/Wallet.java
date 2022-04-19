package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author François VION
 */
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
     */
    public Wallet(Profile accountUser, Bank bank, ArrayList<Account> accountList) {
        this.accountUser = accountUser;
        this.bank = bank;
        this.accountList = accountList;
        this.numberOfAccounts = accountList.size();
    }

    public Wallet(Profile accountUser, Bank bank){
        this.accountUser = accountUser;
        this.bank = bank;
        this.accountList = new ArrayList<>();
    }

    public void fetchArchivedAccount() {

        // Fetch deleted account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+this.accountUser.getNationalRegistrationNumber()+"&hidden=false&deleted=true")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
        this.archivedAccountList = (createsAccountList(response2.getBody()));


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

        this.archivedAccountList.addAll(createsAccountList(response3.getBody()));
    }


    public ArrayList<Account> createsAccountList(String body){
        ArrayList<Account> accountListRep = new ArrayList<>();

        body = body.substring(1, body.length() - 1);
        ArrayList<String> bodyList = Portfolio.JSONArrayParser(body);
        if(!body.equals("")) {
            // Creates the accounts
            for (String s : bodyList) {
                JSONObject obj = new JSONObject(s);
                Profile owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), obj.getJSONObject("account").getString("ownerLastname"), Main.getUser().getUsername(), Main.getUser().getFavoriteLanguage(), obj.getString("userId"));
                Profile coOwner = Main.getUser();
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
                accountListRep.add(new Account(owner, coOwner, this.bank, iban, accountType, activated, archived, canPay));
            }
        }
        return accountListRep;
    }


    /**
     * @return A String to display Wallet information
     */
    @Override
    public String toString() {
        String space = "                    ";
        space = space.substring(0, space.length() - this.bank.getName().length());

        return "Bank : " + this.bank.getName() + space + "SWIFT : " + this.bank.getSwiftCode();
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
