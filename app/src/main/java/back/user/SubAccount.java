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
//    private final double amount; // TODO : A retirer ?

    /**
     * Creates a SubAccount object with an HTTP request by using the IBAN and the currency of an account
     *
     * @param IBAN     The String og the IBAN
     * @param currency The currency
     * @throws UnirestException For managing HTTP errors
     */
    public SubAccount(String IBAN, Currencies currency) {
        this.IBAN = IBAN;
        this.currency = currency;
    }

    public Currencies getCurrency() {
        return this.currency;
    }
}
