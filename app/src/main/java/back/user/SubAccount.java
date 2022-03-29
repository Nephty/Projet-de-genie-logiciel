package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @throws UnirestException For managing HTTP errors
     */
    public SubAccount(String IBAN, Currencies currency) throws UnirestException {
        this.IBAN = IBAN;
        this.currency = currency;
        String token = Main.getToken();
        // Fetch the amount for the subAccount
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://flns-spring-test.herokuapp.com/api/account/sub-account?iban=" + IBAN + "&currencyId=" + "0") // Extension 2 : Change the value of currencyId
                .header("Authorization", "Bearer " + token)
                .asString();
        Main.errorCheck(response.getStatus());
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.amount = obj.getDouble("currentBalance");

        // Fetch all the transactions
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response2 = Unirest.get("https://flns-spring-test.herokuapp.com/api/transaction?iban=" + IBAN + "&currencyId=0")
                .header("Authorization", "Bearer " + Main.getToken())
                .asString();
        Main.errorCheck(response2.getStatus());
        String body2 = response2.getBody();
        body2 = body2.substring(1, body2.length() - 1);
        this.transactionHistory = new ArrayList<>();

        // If there is at least one transaction, it creates the transactions objects
        if (!body2.equals("")) {
            ArrayList<String> parsed = Portfolio.JSONArrayParser(body2);
            for (int i = 0; i < parsed.size(); i++) {
                JSONObject obj2 = new JSONObject(parsed.get(i));
                long ID = obj2.getLong("transactionTypeId");
                String senderName = obj2.getString("senderName");
                String senderIBAN = obj2.getString("senderIban");
                String receiverName = obj2.getString("recipientName");
                String receiverIBAN = obj2.getString("recipientIban");
                double amount = obj2.getDouble("transactionAmount");
                String sendingDate = obj2.getString("transactionDate");
                this.transactionHistory.add(new Transaction(ID, senderName, senderIBAN, receiverName, receiverIBAN, amount, sendingDate, Currencies.EUR));
            }
        }
    }

    public void exportHistory(String path, boolean isCsv){
        try {
            File file;
            if(isCsv){
                file = new File(path + "/transactionHistory.csv");
            } else{
                file = new File(path + "/transactionHistory.json");
            }

            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String res = "";
            if(isCsv){
                for (int i = 0; i < this.transactionHistory.size(); i++) {
                    Transaction t = transactionHistory.get(i);
                    if (t.getSenderIBAN().equals(Main.getCurrentAccount().getIBAN())) {
                        res = convertToCSV(new String[]{t.getSendingDate(), t.getReceiverName(), t.getReceiverIBAN(), ("-"+t.getAmount() + "€")});
                    } else {
                        res = convertToCSV(new String[]{t.getSendingDate(), t.getSenderName(), t.getSenderIBAN(), ("+"+t.getAmount() + "€")});
                    }
                    bw.write(res + "\n");
                }
            } else{
                JSONObject obj = new JSONObject();
                ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
                for (int i = 0; i < this.transactionHistory.size(); i++) {
                    Transaction t = transactionHistory.get(i);
                    JSONObject obj2 = new JSONObject();
                    obj2.put("sendingDate", t.getSendingDate());
                    obj2.put("senderName", t.getSenderName());
                    obj2.put("senderIBAN", t.getSenderIBAN());
                    obj2.put("receiverName", t.getReceiverName());
                    obj2.put("receiverIBAN", t.getReceiverIBAN());
                    obj2.put("amount", t.getAmount());
                    jsonList.add(obj2);
                }
                obj.put("transactionList", jsonList);
                 res = obj.toString();
                 bw.write(res);
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public Currencies getCurrency() {
        return this.currency;
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
}
