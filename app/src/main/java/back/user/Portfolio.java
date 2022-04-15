package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Portfolio {
    private Profile user;
    private ArrayList<Wallet> walletList;


    /**
     * Creates a portfolio with an HTTP request by using the user's national registration code
     *
     * @param nationalRegistrationNumber The user's national registration code
     * @throws UnirestException For managing HTTP errors
     */
    public Portfolio(String nationalRegistrationNumber) {
        this.user = new Profile(nationalRegistrationNumber);
        // It fetchs all the access that got the user
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(()-> {
            HttpResponse<String> rep = null;
            try {
                 rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId=" + nationalRegistrationNumber + "&deleted=false&hidden=false")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        Main.errorCheck(response.getStatus());

        String body = response.getBody();
        body = body.substring(1, body.length() - 1);

        // Fetch deleted account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response2;
        try {
            response2 = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+ nationalRegistrationNumber +"&hidden=false&deleted=true")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        String body2 = response2.getBody();
        body2 = body2.substring(1, body2.length() - 1);

        // Fetch hidden account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response3;
        try {
            response3 = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+nationalRegistrationNumber+"&hidden=true&deleted=false")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        String body3 = response3.getBody();
        body3 = body3.substring(1, body3.length() - 1);

        System.out.println("------------------------------------");
        System.out.println(body);
        System.out.println(body2);
        System.out.println(body3);
        System.out.println("------------------------------------");
        this.walletList = createWallets(body,body2,body3);
    }

    public ArrayList<Wallet> createWallets(String body, String body2, String body3) {
        ArrayList<Wallet> repWalletList = new ArrayList<Wallet>();
        // If the user got at least one account, it will parse the list of account access into list of account in the same bank (Wallet)
        if (!body.equals("")) {
            ArrayList<String> bodyList = JSONArrayParser(body);

            ArrayList<Account> accountList = new ArrayList<Account>();

            String oldSwift = "";
            Bank bank = null;
            for (int i = 0; i < bodyList.size(); i++) {
                JSONObject obj = new JSONObject(bodyList.get(i));
                String swift = obj.getJSONObject("account").getString("swift");
                if (oldSwift.equals(swift) || i == 0) {
                    if (i == 0) {
                        oldSwift = swift;
                        String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                        bank = new Bank(swift, bankName);
                    }
                    // TODO : coOwner quand ce sera implémentée dans l'API
                    Profile owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), Main.getUser().getFavoriteLanguage(), obj.getJSONObject("account").getString("ownerLastname"), obj.getJSONObject("account").getString("userId"));
                    //Profile coOwner = new Profile(obj.getJSONObject("userId").getString("firstname"), obj.getJSONObject("userId").getString("lastname"), obj.getJSONObject("userId").getString("userID"));
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
                    // TODO : Remettre coOwner
                    accountList.add(new Account(owner, owner, bank, iban, accountType, activated, archived, canPay));
                } else {
                    repWalletList.add(new Wallet(this.user, bank, accountList));
                    accountList = new ArrayList<Account>();
                    String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                    bank = new Bank(swift, bankName);
                    oldSwift = swift;
                    Profile owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), obj.getJSONObject("account").getString("ownerLastname"), Main.getUser().getFavoriteLanguage(), obj.getJSONObject("account").getString("userId"));
                    //Profile coOwner = new Profile(obj.getJSONObject("userId").getString("firstname"), obj.getJSONObject("userId").getString("lastname"), obj.getJSONObject("userId").getString("userID"));
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
                    // TODO : Remettre coOwner
                    accountList.add(new Account(owner, owner, bank, iban, accountType, activated, archived, canPay));
                }
            }
            repWalletList.add(new Wallet(this.user, bank, accountList));
        }

        // Manage case where the client only get deleted accounts in a bank
        // Fetch all banks created
        ArrayList<String> usedSwift = new ArrayList<String>();
        for (int i = 0; i < repWalletList.size(); i++) {
            usedSwift.add(repWalletList.get(i).getBank().getSwiftCode());
        }

        if (!body2.equals("")) {
            ArrayList<String> bodyList2 = JSONArrayParser(body2);
            for (int i = 0; i < bodyList2.size(); i++) {
                JSONObject obj = new JSONObject(bodyList2.get(i));
                String swift = obj.getJSONObject("account").getString("swift");
                String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                if (!usedSwift.contains(swift)) {
                    repWalletList.add(new Wallet(this.user, new Bank(swift, bankName)));
                    usedSwift.add(swift);
                }
            }
        }

        if (!body3.equals("")) {
            ArrayList<String> bodyList3 = JSONArrayParser(body3);
            for (int i = 0; i < bodyList3.size(); i++) {
                JSONObject obj = new JSONObject(bodyList3.get(i));
                String swift = obj.getJSONObject("account").getString("swift");
                String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                if (!usedSwift.contains(swift)) {
                    repWalletList.add(new Wallet(this.user, new Bank(swift, bankName)));
                    usedSwift.add(swift);
                }
            }
        }
        return repWalletList;
    }



    /**
     * A method for parsing arrays in JSON
     *
     * @param json The String to parse
     * @return A list of parsed Strings
     */
    public static ArrayList<String> JSONArrayParser(String json) {
        ArrayList<String> rep = new ArrayList<>();
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

    // Used for tests
    public Portfolio(Profile user){
        this.user = user;
    }
}
