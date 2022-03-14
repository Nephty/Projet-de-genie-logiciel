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


    public Portfolio(String nationalRegistrationNumber) throws UnirestException {
        this.user = new Profile(nationalRegistrationNumber);
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account-access/" + nationalRegistrationNumber)
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();

        String body = response.getBody();
        body = body.substring(1, body.length() - 1);

        ArrayList<String> bodyList = JSONArrayParser(body);

        ArrayList<Account> accountList = new ArrayList<Account>();

        String oldSwift = "";
        Bank bank = null;
        this.walletList = new ArrayList<Wallet>();
        for(int i = 0; i<bodyList.size();i++){
            JSONObject obj = new JSONObject(bodyList.get(i));
            // TODO : A optimiser ?
            String swift = obj.getJSONObject("accountId").getJSONObject("swift").getString("swift");
            if(oldSwift == swift || i == 0) {
                if(i == 0){
                    oldSwift = swift;
                    bank = new Bank(swift);
                }
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
            } else {
                walletList.add(new Wallet(this.user, bank, accountList));
                accountList.clear();
                bank = new Bank(swift);
                oldSwift = swift;
            }
            walletList.add(new Wallet(this.user, bank, accountList));
        }
        System.out.println(walletList.get(0).getAccountList().get(0).getIBAN());
    }


    public Profile getUser(){
        return this.user;
    }

    public ArrayList<Wallet> getWalletList(){
        return this.walletList;
    }

    private ArrayList<String> JSONArrayParser(String json) {
        ArrayList<String> rep = new ArrayList<String>();
        int crochet = 0;
        int save = 0;
        for(int i = 0; i < json.length(); i++){
            if(json.charAt(i) == '{'){
                crochet++;
            }
            if(json.charAt(i) == '}'){
                crochet--;
            }
            if(json.charAt(i) == ',' && crochet == 0){
                rep.add(json.substring(save, i));
                save = i + 1;
            }
        }
        rep.add(json.substring(save, json.length()));
        return rep;
    }
    }
