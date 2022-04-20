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
public class SubAccount {
    private final String IBAN;
    private final Currencies currency;
    private final double amount;
    private ArrayList<Transaction> transactionHistory;

    /**
     * Creates a SubAccount object with an HTTP request by using the IBAN and the currency of an account
     *
     * @param IBAN     The String og the IBAN
     * @param currency The currency
     */
    public SubAccount(String IBAN, Currencies currency) {
        this.IBAN = IBAN;
        this.currency = currency;

        // Fetch the information of the SubAccount
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/account/sub-account?iban=" + IBAN + "&currencyId=" + "0")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
        Main.errorCheck(response.getStatus());
        String body = response.getBody();
        // Get the amount of the SubAccount
        JSONObject obj = new JSONObject(body);
        this.amount = obj.getDouble("currentBalance");

        // Fetch all the transactions
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/transaction?iban=" + IBAN + "&currencyId=" + "0")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        // Check the HTTP code status to inform the user if there is an error
        Main.errorCheck(response2.getStatus());

        String body2 = response2.getBody();
        body2 = body2.substring(1, body2.length() - 1);
        this.transactionHistory = new ArrayList<>();

        // If there is at least one transaction, it creates the transactions objects
        if (!body2.equals("")) {
            ArrayList<String> parsed = Portfolio.JSONArrayParser(body2);
            for (String s : parsed) {
                JSONObject obj2 = new JSONObject(s);
                long ID = obj2.getLong("transactionTypeId");
                String senderName = obj2.getString("senderName");
                String senderIBAN = obj2.getString("senderIban");
                String receiverName = obj2.getString("recipientName");
                String receiverIBAN = obj2.getString("recipientIban");
                double amount = obj2.getDouble("transactionAmount");
                String sendingDate = obj2.getString("transactionDate");
                String message = obj2.getString("comments");
                this.transactionHistory.add(new Transaction(ID, senderName, senderIBAN, receiverName, receiverIBAN, amount, sendingDate, Currencies.EUR, message));
            }
        }
    }

    public double getAmount() {
        return this.amount;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return this.transactionHistory;
    }

    public String getIBAN() {
        return IBAN;
    }

    @Override
    public String toString() {
        return IBAN + "     " + amount + currency;
    }
}
