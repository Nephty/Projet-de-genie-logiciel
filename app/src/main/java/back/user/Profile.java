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

public class Profile {
    private final String firstName;
    private final String lastName;
    private final String nationalRegistrationNumber;


    /**
     * Creates a Profile object with an HTTP request by using the user's national registration number
     *
     * @param nationalRegistrationNumber The String of the user's national registration number
     * @throws UnirestException For managing HTTP errors
     */
    public Profile(String nationalRegistrationNumber) {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber + "?isUsername=false")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        Main.errorCheck(response.getStatus());
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);
        this.firstName = obj.getString("firstname");
        this.lastName = obj.getString("lastname");
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }


    /**
     * Creates a Profile object by giving all the needed informations
     *
     * @param firstName                  A string of the firstname
     * @param lastName                   A string of the lastname
     * @param nationalRegistrationNumber A string of the national registration number
     */
    public Profile(String firstName, String lastName, String nationalRegistrationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
    }


    /**
     * Fetches all the customers of a bank
     *
     * @param swift The String of the ban's swift
     * @return An ArrayList of all the customers
     */
    public static ArrayList<Profile> fetchAllCustomers(String swift) {
        ArrayList<Profile> rep = new ArrayList<Profile>();
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/customer")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
            Main.errorCheck(response.getStatus());
        } catch (UnirestException e) {
            Main.ErrorManager(408);
        }

        String body = response.getBody();
        body = body.substring(1, body.length() - 1);

        if (!body.equals("")) {
            ArrayList<String> customerList = Bank.JSONArrayParser(body);
            ArrayList<String> userIdList = new ArrayList<String>();
            for (int i = 0; i < customerList.size(); i++) {
                JSONObject obj = new JSONObject(customerList.get(i));
                if (!userIdList.contains(obj.getString("userId"))) {
                    rep.add(new Profile(obj.getString("firstname"), obj.getString("lastname"), obj.getString("userId")));
                    userIdList.add(obj.getString("userId"));
                }
            }
        }
        return rep;
    }

    public static void exportClientData(ArrayList<Profile> clientList, String path, boolean isCsv){
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

            ArrayList<Wallet> walletList = new ArrayList<Wallet>();
            for(int i = 0; i<clientList.size(); i++){
                walletList.add(new Wallet(clientList.get(i)));
            }

            if(isCsv){
                for (int i = 0; i < walletList.size(); i++) {
                    Wallet wallet = walletList.get(i);
                    ArrayList<Account> accountList = wallet.getAccountList();
                    String resAccount = "[";
                    for(int j = 0; j<accountList.size(); j++){
                        Account account = accountList.get(j);
                        SubAccount subAccount = account.getSubAccountList().get(0);
                        if(j == 0 || j == accountList.size() -1){
                            resAccount = resAccount + account.getIBAN()+" "+account.getAccountType()+" "+ account.canPay()+" "+account.isActivated();
                        }else{
                            resAccount = resAccount + ","+account.getIBAN()+" "+account.getAccountType()+" "+ account.canPay()+" "+account.isActivated();
                        }
                    }
                    resAccount = resAccount + "]";
                    Profile client = wallet.getAccountUser();
                    res = convertToCSV(new String[]{client.getFirstName(), client.getLastName(), client.getNationalRegistrationNumber(), resAccount});
                    bw.write(res + "\n");
                }
            } else{
//                JSONObject obj = new JSONObject();
//                ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
//                for (int i = 0; i < this.transactionHistory.size(); i++) {
//                    Transaction t = transactionHistory.get(i);
//                    JSONObject obj2 = new JSONObject();
//                    obj2.put("sendingDate", t.getSendingDate());
//                    obj2.put("senderName", t.getSenderName());
//                    obj2.put("senderIBAN", t.getSenderIBAN());
//                    obj2.put("receiverName", t.getReceiverName());
//                    obj2.put("receiverIBAN", t.getReceiverIBAN());
//                    obj2.put("amount", t.getAmount());
//                    jsonList.add(obj2);
//                }
//                obj.put("transactionList", jsonList);
//                res = obj.toString();
//                bw.write(res);
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(Profile::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * @return A String to display the profile informations
     */
    @Override
    public String toString() {
        return this.firstName + "   " + this.lastName + "     " + this.nationalRegistrationNumber;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getNationalRegistrationNumber() {
        return this.nationalRegistrationNumber;
    }
}
