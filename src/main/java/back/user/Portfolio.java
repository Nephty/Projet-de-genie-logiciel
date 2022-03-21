package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Portfolio {
    private final Profile user;
    private final ArrayList<Wallet> walletList;


    /**
     * Creates a portfolio with an HTTP request by using the user's national registration code
     * @param nationalRegistrationNumber The user's national registration code
     * @throws UnirestException For managing HTTP errors
     */
    public Portfolio(String nationalRegistrationNumber) throws UnirestException {
        this.user = new Profile(nationalRegistrationNumber);
        // It fetchs all the access that got the user
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/" + nationalRegistrationNumber)
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();

        String body = response.getBody();
        body = body.substring(1, body.length() - 1);
        this.walletList = new ArrayList<Wallet>();
        // If the user got at least one account, it will parse the list of account access into list of account in the same bank (Wallet)
        if (!body.equals("")) {
            ArrayList<String> bodyList = JSONArrayParser(body);

            ArrayList<Account> accountList = new ArrayList<Account>();

            String oldSwift = "";
            Bank bank = null;
            for (int i = 0; i < bodyList.size(); i++) {
                JSONObject obj = new JSONObject(bodyList.get(i));
                String swift = obj.getJSONObject("accountId").getJSONObject("swift").getString("swift");
                if (oldSwift.equals(swift) || i == 0) {
                    if (i == 0) {
                        oldSwift = swift;
                        bank = new Bank(swift);
                    }
                    Profile owner = new Profile(obj.getJSONObject("accountId").getJSONObject("userId").getString("firstname"), obj.getJSONObject("accountId").getJSONObject("userId").getString("lastname"), obj.getJSONObject("accountId").getJSONObject("userId").getString("userID"));
                    Profile coOwner = new Profile(obj.getJSONObject("userId").getString("firstname"), obj.getJSONObject("userId").getString("lastname"), obj.getJSONObject("userId").getString("userID"));
                    String iban = obj.getJSONObject("accountId").getString("iban");
                    int accountTypeId = obj.getJSONObject("accountId").getJSONObject("accountTypeId").getInt("accountTypeId");
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
                    boolean activated = obj.getBoolean("access");
                    boolean archived = obj.getBoolean("hidden");
                    boolean canPay = obj.getJSONObject("accountId").getBoolean("payment");
                    accountList.add(new Account(owner, coOwner, bank, iban, accountType, activated, archived, canPay));
                } else {
                    this.walletList.add(new Wallet(this.user, bank, accountList));
                    accountList = new ArrayList<Account>();
                    bank = new Bank(swift);
                    oldSwift = swift;
                    Profile owner = new Profile(obj.getJSONObject("accountId").getJSONObject("userId").getString("userID"));
                    Profile coOwner = new Profile(obj.getJSONObject("userId").getString("userID"));
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
            this.walletList.add(new Wallet(this.user, bank, accountList));
        }
    }

    /**
     * A method for parsing arrays in JSON
     * @param json The String to parse
     * @return A list of parsed Strings
     */
    public static ArrayList<String> JSONArrayParser(String json) {
        ArrayList<String> rep = new ArrayList<String>();
        int crochet = 0;
        int save = 0;
        for (int i = 0; i < json.length(); i++) {
            if (json.charAt(i) == '{') {
                crochet++;
            }
            if (json.charAt(i) == '}') {
                crochet--;
            }
            if (json.charAt(i) == ',' && crochet == 0) {
                rep.add(json.substring(save, i));
                save = i + 1;
            }
        }
        rep.add(json.substring(save));
        return rep;
    }

    public Profile getUser() {
        return this.user;
    }

    public ArrayList<Wallet> getWalletList() {
        return this.walletList;
    }
}
