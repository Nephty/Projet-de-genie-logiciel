package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubAccount {
    private final String IBAN;
    private final Currencies currency;
    private final double amount;
    private ArrayList<Transaction> transactionHistory;


    public SubAccount(String IBAN, Currencies currency) throws UnirestException {
        this.IBAN = IBAN;
        this.currency = currency;
        String token = Main.getToken();
        // TODO : Instancier les valeurs grâce à l'IBAN et la Currency
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account/sub-account?iban=" + IBAN + "&currencyId=" + "0") // Extension 2 : Changer la valeur de 0 en fonction de la monnaie
                .header("Authorization", "Bearer " + token)
                .asString();
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.amount = obj.getDouble("currentBalance");
        // TODO : Requetes transactions
    }

    public Currencies getCurrency() {
        return this.currency;
    }

    public double getAmount() {
        return this.amount;
    }
}
