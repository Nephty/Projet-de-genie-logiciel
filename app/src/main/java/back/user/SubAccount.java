package back.user;


import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class SubAccount {
    private final String IBAN;
    private final Currencies currency;
    private final double amount;

    /**
     * Creates a SubAccount object with an HTTP request by using the IBAN and the currency of an account
     *
     * @param IBAN     The String og the IBAN
     * @param currency The currency
     */
    public SubAccount(String IBAN, Currencies currency) {
        this.IBAN = IBAN;
        this.currency = currency;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account/sub-account?iban="+this.IBAN +"&currencyId=0")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
        Main.errorCheck(response.getStatus());
        JSONObject obj = new JSONObject(response.getBody());
        this.amount = obj.getDouble("currentBalance");
    }

    public SubAccount(String IBAN, Currencies currency, double amount) {
        this.IBAN = IBAN;
        this.currency = currency;
        this.amount = amount;
    }
        public double getAmount() {
        return amount;
    }
}
