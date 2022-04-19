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
public class Portfolio {
    private final Profile user;
    private ArrayList<Wallet> walletList;


    /**
     * Creates a portfolio with an HTTP request by using the user's national registration code
     *
     * @param nationalRegistrationNumber The user's national registration code
     */
    public Portfolio(String nationalRegistrationNumber) {
        System.out.println(Main.getToken());
        System.out.println(Main.getRefreshToken());
        // TODO : Optimiser en reprenant l'ancien user si il existe ?
        this.user = new Profile(nationalRegistrationNumber);
        // It fetches all the access that got the user
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(()-> {
            HttpResponse<String> rep;
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
        HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+ nationalRegistrationNumber +"&hidden=false&deleted=true")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        String body2 = response2.getBody();
        body2 = body2.substring(1, body2.length() - 1);

        // Fetch hidden account
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response3 = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/all?userId="+nationalRegistrationNumber+"&hidden=true&deleted=false")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });


        String body3 = response3.getBody();
        body3 = body3.substring(1, body3.length() - 1);

        this.walletList = createWallets(body,body2,body3);
    }

    public ArrayList<Wallet> createWallets(String body, String body2, String body3) {
        ArrayList<Wallet> repWalletList = new ArrayList<>();
        // If the user got at least one account, it will parse the list of account access into list of account in the same bank (Wallet)
        if (!body.equals("")) {
            ArrayList<String> bodyList = JSONArrayParser(body);

            ArrayList<Account> accountList = new ArrayList<>();

            String oldSwift = "";
            Bank bank = null;
            for (int i = 0; i < bodyList.size(); i++) {
                JSONObject obj = new JSONObject(bodyList.get(i));
                String swift = obj.getJSONObject("account").getString("swift");
                Profile owner;
                Profile coOwner;
                if (oldSwift.equals(swift) || i == 0) {
                    if (i == 0) {
                        oldSwift = swift;
                        String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                        bank = new Bank(swift, bankName);
                    }
                    owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), Main.getUser().getUsername(), Main.getUser().getFavoriteLanguage(), obj.getJSONObject("account").getString("ownerLastname"), obj.getJSONObject("account").getString("userId"));
                    coOwner = Main.getUser();
                } else {
                    repWalletList.add(new Wallet(this.user, bank, accountList));
                    accountList = new ArrayList<>();
                    String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                    bank = new Bank(swift, bankName);
                    oldSwift = swift;
                    owner = new Profile(obj.getJSONObject("account").getString("ownerFirstname"), obj.getJSONObject("account").getString("ownerLastname"), Main.getUser().getUsername(), Main.getUser().getFavoriteLanguage(), obj.getJSONObject("account").getString("userId"));
                    coOwner = Main.getUser();
                }
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
                accountList.add(new Account(owner, coOwner, bank, iban, accountType, activated, archived, canPay));
            }
            repWalletList.add(new Wallet(this.user, bank, accountList));
        }

        // Manage case where the client only get deleted accounts in a bank
        // Fetch all banks created
        ArrayList<String> usedSwift = new ArrayList<>();
        for (Wallet wallet : repWalletList) {
            usedSwift.add(wallet.getBank().getSwiftCode());
        }

        class LocalParser {
            protected void localParse(String body, ArrayList<String> usedSwift, ArrayList<Wallet> repWalletList, Profile user) {
                if (!body.equals("")) {
                    ArrayList<String> bodyList = JSONArrayParser(body);
                    for (String s : bodyList) {
                        JSONObject obj = new JSONObject(s);
                        String swift = obj.getJSONObject("account").getString("swift");
                        String bankName = obj.getJSONObject("account").getJSONObject("linkedBank").getString("name");
                        if (!usedSwift.contains(swift)) {
                            repWalletList.add(new Wallet(user, new Bank(swift, bankName)));
                            usedSwift.add(swift);
                        }
                    }
                }
            }
        }

        LocalParser localParser = new LocalParser();
        localParser.localParse(body2, usedSwift, repWalletList, user);
        localParser.localParse(body3, usedSwift, repWalletList, user);

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
