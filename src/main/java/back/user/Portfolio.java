package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

        HashMap<String, ArrayList<String>> IBANList = new HashMap<String, ArrayList<String>>();

        for(int i = 0; i<bodyList.size();i++){
            JSONObject obj = new JSONObject(bodyList.get(i));
            String swift = obj.getJSONObject("accountId").getJSONObject("swift").getString("swift");
            String iban = obj.getJSONObject("accountId").getString("iban");
            if(IBANList.containsKey(swift)){
                ArrayList<String> array = IBANList.get(swift);
                array.add(iban);
                IBANList.replace(swift, array);
            } else{
                ArrayList<String> array = new ArrayList<String>();
                array.add(iban);
                IBANList.put(swift, array);
            }
        }

        this.walletList = new ArrayList<Wallet>();
        for (String i : IBANList.keySet()){
            this.walletList.add(new Wallet(this.user, new Bank(i), IBANList.get(i)));
        }
        System.out.println(walletList);
        System.out.println(walletList.get(0).getAccountUser());
    }

    public static ArrayList<String> JSONArrayParser(String json){
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

    public Profile getUser(){
        return this.user;
    }

    public ArrayList<Wallet> getWalletList(){
        return this.walletList;
    }
}
