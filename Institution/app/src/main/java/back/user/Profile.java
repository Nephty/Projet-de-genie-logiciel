package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.*;

import java.io.*;
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
     */
    public Profile(String nationalRegistrationNumber) {
        // Fetch the user's information in the database
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + nationalRegistrationNumber + "?isUsername=false")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });

        // Parse the information and put them in the variables
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

        // Fetch the list of customers
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep2 = null;
            try {
                rep2 = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/customer")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep2;
        });

        return parseCustomer(response.getBody());
    }


    /**
     * Parse all the customers in a JSON into a list of customers
     * @param json  The JSON to parse
     * @return      The list of the customers
     */
    public static ArrayList<Profile> parseCustomer(String json){
        ArrayList<Profile> rep = new ArrayList<Profile>();
        String body = json;
        body = body.substring(1, body.length() - 1);
        // If there is at least one customer, it parse the list and create the objects to put them in the final list
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

    /**
     * Import a file's data and put them in the database
     * @param path  The String of the file's path to import
     */
    public static void importData(String path) {
        // Read the file and convert it into JSONObject
        Object parser = null;
        try {
            parser = new JSONParser().parse(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create all the objects in the file
        String str = parser.toString();
        JSONObject obj = new JSONObject(str);
        JSONArray clientList = obj.getJSONArray("clientList");
        ArrayList<Wallet> walletList = new ArrayList<Wallet>();
        Bank bank = Main.getBank();
        for(int i = 0; i<clientList.length(); i++){
            JSONObject clientInfos = clientList.getJSONObject(i);
            Profile client = new Profile(clientInfos.getString("clientFirstname"),clientInfos.getString("clientLastname"), clientInfos.getString("clientID"));
            JSONArray accountListJSON = clientInfos.getJSONArray("accountList");
            ArrayList<Account> accountList = new ArrayList<Account>();
            for(int j = 0; j<accountListJSON.length(); j++){
                JSONObject objAccount = (JSONObject) accountListJSON.get(j);
                AccountType accType = AccountType.valueOf(objAccount.getString("accountType"));
                Profile owner = new Profile(objAccount.getString("owner"));
                Account account = new Account(owner, client, bank, objAccount.getString("IBAN"), accType, objAccount.getBoolean("activated"), false, objAccount.getBoolean("canPay"), objAccount.getDouble("amount"));
                accountList.add(account);
            }
            walletList.add(new Wallet(client, accountList));
        }

        // Puts the data in the database
        String swift = Main.getBank().getSwiftCode();

        for(int i = 0; i< walletList.size(); i++){

            // Creates account with all the values
            String userId = walletList.get(i).getAccountUser().getNationalRegistrationNumber();
            ArrayList<Account> accountList = walletList.get(i).getAccountList();

            for(int j = 0; j<accountList.size(); j++){
                String IBAN = accountList.get(j).getIBAN();
                AccountType accountType = accountList.get(j).getAccountType();
                int accType = 0;
                switch (accountType) {
                    case COURANT:
                        accType = 1;
                        break;
                    case JEUNE:
                        accType = 2;
                        break;
                    case EPARGNE:
                        accType = 3;
                        break;
                    case TERME:
                        accType = 4;
                        break;
                }

                int finalAccType = accType;
                String ownerId = accountList.get(j).getAccountOwner().getNationalRegistrationNumber();
                // Creates the account
                Unirest.setTimeouts(0, 0);
                HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                    HttpResponse<String> rep = null;
                    try {
                        rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account")
                                .header("Authorization", "Bearer " + Main.getToken())
                                .header("Content-Type", "application/json")
                                .body("{\r\n    \"iban\": \"" + IBAN + "\",\r\n    \"swift\": \"" + swift + "\",\r\n    \"userId\": \"" + ownerId + "\",\r\n    \"payment\": false,\r\n    \"accountTypeId\": " + finalAccType + "\r\n}")
                                .asString();
                    } catch (UnirestException e) {
                        throw new RuntimeException(e);
                    }
                    // Ignore the conflict error
                    if(rep.getStatus() == 409){
                        return null;
                    } else{
                        return rep;
                    }
                });

                // Create account access
                Unirest.setTimeouts(0, 0);
                HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
                    HttpResponse<String> rep = null;
                    try {
                        rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account-access")
                                .header("Authorization", "Bearer " + Main.getToken())
                                .header("Content-Type", "application/json")
                                .body("{\r\n    \"accountId\": \"" + IBAN + "\",\r\n    \"userId\": \"" + userId + "\",\r\n    \"access\": true,\r\n    \"hidden\": false\r\n}")
                                .asString();
                    } catch (UnirestException e) {
                        throw new RuntimeException(e);
                    }
                    // Ignore the conflict error
                    if(rep.getStatus() == 409){
                        return null;
                    } else{
                        return rep;
                    }
                });
            }
        }
    }


    /**
     * Export all the client's data
     * @param clientList    The list of the client to export
     * @param path          The path of the exported file
     * @param isCsv         If the file is a CSV or a JSON
     */
    public static void exportClientData(ArrayList<Profile> clientList, String path, boolean isCsv){
        try {
            // Creates the file
            File file = new File(path + "/clientData" + (isCsv ? ".csv" : ".json"));

            boolean fileCreated = file.createNewFile();

            int counter = 0;
            while (!fileCreated) {
                file = new File(path + "/clientData" + counter + (isCsv ? ".csv" : ".json"));
                counter++;
                fileCreated = file.createNewFile();
            }


            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String res = "";

            // Fetchs the wallet list of the clients
            ArrayList<Wallet> walletList = new ArrayList<Wallet>();
            for(int i = 0; i<clientList.size(); i++){
                walletList.add(new Wallet(clientList.get(i)));
            }

            if(isCsv){
                // Write a line for each client in the csv file
                for (int i = 0; i < walletList.size(); i++) {
                    Wallet wallet = walletList.get(i);
                    ArrayList<Account> accountList = wallet.getAccountList();
                    String resAccount = "[";
                    for(int j = 0; j<accountList.size(); j++){
                        Account account = accountList.get(j);
                        if(j == 0 || j == accountList.size() -1){
                            resAccount = resAccount + account.getIBAN()+" "+account.getAccountType().toString()+" "+account.getSubAccountList().get(0).getAmount() +"€ "+ account.canPay()+" "+account.isActivated()+" "+account.getAccountOwner().getNationalRegistrationNumber();
                        }else{
                            resAccount = resAccount + ","+account.getIBAN()+" "+account.getAccountType().toString()+" "+account.getSubAccountList().get(0).getAmount() +"€ "+ account.canPay()+" "+account.isActivated()+" "+account.getAccountOwner().getNationalRegistrationNumber();
                        }
                    }
                    resAccount = resAccount + "]";
                    Profile client = wallet.getAccountUser();
                    res = convertToCSV(new String[]{client.getFirstName(), client.getLastName(), client.getNationalRegistrationNumber(), resAccount});
                    bw.write(res + "\n");
                }
            } else{
                // Create a JSON with all the clients data and write it in the file
                JSONObject obj = new JSONObject();
                ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
                for (int i = 0; i < walletList.size(); i++) {
                    Wallet wallet = walletList.get(i);
                    ArrayList<Account> accountList = wallet.getAccountList();
                    Profile client = wallet.getAccountUser();
                    ArrayList<JSONObject> infos = new ArrayList<JSONObject>();
                    for(int j = 0; j<accountList.size(); j++) {
                        Account account = accountList.get(j);
                        JSONObject accountInfo = new JSONObject();
                        accountInfo.put("IBAN", account.getIBAN());
                        accountInfo.put("accountType", account.getAccountType().toString());
                        accountInfo.put("amount", account.getSubAccountList().get(0).getAmount());
                        accountInfo.put("canPay", String.valueOf(account.canPay()));
                        accountInfo.put("activated", String.valueOf(account.isActivated()));
                        accountInfo.put("owner", account.getAccountOwner().getNationalRegistrationNumber());
                        infos.add(accountInfo);
                    }
                    JSONObject obj2 = new JSONObject();
                    obj2.put("clientFirstname", client.getFirstName());
                    obj2.put("clientLastname", client.getLastName());
                    obj2.put("clientID", client.getNationalRegistrationNumber());
                    obj2.put("accountList", infos);
                    jsonList.add(obj2);
                }
                obj.put("clientList", jsonList);
                res = obj.toString();
                bw.write(res);
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert a String list into a CSV line
     * @param data  A list of String to formate into a CSV line
     * @return      The String of the CSV line
     */
    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(Profile::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    /**
     * Escape the special characters to avoid problems in CSV files
     * @param data  The String to manage
     * @return      The String with the special characters escaped
     */
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
