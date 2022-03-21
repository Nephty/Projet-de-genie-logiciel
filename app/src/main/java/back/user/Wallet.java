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
     * @throws UnirestException
     */
    public Wallet(Profile accountUser) throws UnirestException {
        this.accountUser = accountUser;
        this.bank = Main.getBank();
        accountList = new ArrayList<Account>();

        // Fetch all client's account access
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/" + this.accountUser.getNationalRegistrationNumber())
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();

        String body = response.getBody();
        body = body.substring(1, body.length() - 1);

        ArrayList<String> bodyList = Bank.JSONArrayParser(body);
        // Creates the accounts
        for (int i = 0; i < bodyList.size(); i++) {
            JSONObject obj = new JSONObject(bodyList.get(i));
            String swift = obj.getJSONObject("accountId").getJSONObject("swift").getString("swift");
            Profile owner = new Profile(obj.getJSONObject("accountId").getJSONObject("userId").getString("firstname"), obj.getJSONObject("accountId").getJSONObject("userId").getString("lastname"), obj.getJSONObject("accountId").getJSONObject("userId").getString("userID"));
            Profile coOwner = new Profile(obj.getJSONObject("userId").getString("firstname"), obj.getJSONObject("userId").getString("lastname"), obj.getJSONObject("userId").getString("userID"));
            String iban = obj.getJSONObject("accountId").getString("iban");
            int accountTypeId = obj.getJSONObject("accountId").getJSONObject("accountTypeId").getInt("accountTypeId");
            AccountType accountType = null;
            switch (accountTypeId) {
                case 0:
                    accountType = AccountType.COURANT;
                    break;
                case 1:
                    accountType = AccountType.JEUNE;
                    break;
                case 2:
                    accountType = AccountType.EPARGNE;
                    break;
                case 3:
                    accountType = AccountType.TERME;
                    break;
            }
            boolean activated = obj.getBoolean("access");
            boolean archived = obj.getBoolean("hidden");
            boolean canPay = obj.getJSONObject("accountId").getBoolean("payment");
            accountList.add(new Account(owner, coOwner, bank, iban, accountType, activated, archived, canPay));
        }
    }


    public void update() throws UnirestException {
        this.accountList = new ArrayList<Account>();

        // Fetch all client's account access
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/" + this.accountUser.getNationalRegistrationNumber())
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();

        String body = response.getBody();
        body = body.substring(1, body.length() - 1);

        ArrayList<String> bodyList = Bank.JSONArrayParser(body);
        // Creates the accounts
        for (int i = 0; i < bodyList.size(); i++) {
            JSONObject obj = new JSONObject(bodyList.get(i));
            String swift = obj.getJSONObject("accountId").getJSONObject("swift").getString("swift");
            Profile owner = new Profile(obj.getJSONObject("accountId").getJSONObject("userId").getString("firstname"), obj.getJSONObject("accountId").getJSONObject("userId").getString("lastname"), obj.getJSONObject("accountId").getJSONObject("userId").getString("userID"));
            Profile coOwner = new Profile(obj.getJSONObject("userId").getString("firstname"), obj.getJSONObject("userId").getString("lastname"), obj.getJSONObject("userId").getString("userID"));
            String iban = obj.getJSONObject("accountId").getString("iban");
            int accountTypeId = obj.getJSONObject("accountId").getJSONObject("accountTypeId").getInt("accountTypeId");
            AccountType accountType = null;
            switch (accountTypeId) {
                case 0:
                    accountType = AccountType.COURANT;
                    break;
                case 1:
                    accountType = AccountType.JEUNE;
                    break;
                case 2:
                    accountType = AccountType.EPARGNE;
                    break;
                case 3:
                    accountType = AccountType.TERME;
                    break;
            }
            boolean activated = obj.getBoolean("access");
            boolean archived = obj.getBoolean("hidden");
            boolean canPay = obj.getJSONObject("accountId").getBoolean("payment");
            this.accountList.add(new Account(owner, coOwner, bank, iban, accountType, activated, archived, canPay));
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
