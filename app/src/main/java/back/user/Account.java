package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.naming.SizeLimitExceededException;
import java.util.ArrayList;

public class Account {
    private final Profile accountOwner;
    private final ArrayList<Profile> accountCoOwner;
    private final Bank bank;
    private final String IBAN;
    private final AccountType accountType;
    private final boolean archived;
    private final boolean canPay;
    private final ArrayList<SubAccount> subAccountList;
    private boolean activated;
    private int numberOfSubAccounts = 0;


    /**
     * Creates an Account object with all the informations needed
     *
     * @param accountOwner   The owner of the account in a Profile object
     * @param accountCoOwner A co owner of the account in a Profile object
     * @param bank           The bank where the account is. In a Bank object
     * @param IBAN           A String of the IBAN
     * @param accountType    The type of the account in accountType enumeration
     * @param activated      A boolean for the activated/desactivated option
     * @param archived       A boolean for archived/unarchived option
     * @param canPay         A boolean for canPay/cannotPay option
     * @throws UnirestException For managing HTTP errors
     */
    public Account(Profile accountOwner, Profile accountCoOwner, Bank bank, String IBAN, AccountType accountType, boolean activated, boolean archived, boolean canPay) throws UnirestException {
        this.accountOwner = accountOwner;
        this.accountCoOwner = new ArrayList<Profile>();
        this.accountCoOwner.add(accountCoOwner);
        this.bank = bank;
        this.IBAN = IBAN;
        this.accountType = accountType;
        this.activated = activated;
        this.archived = archived;
        this.canPay = canPay;
        this.subAccountList = new ArrayList<SubAccount>();
        this.subAccountList.add(new SubAccount(this.IBAN, Currencies.EUR));
        this.numberOfSubAccounts = this.subAccountList.size();
    }

    /**
     * @return A String to display the account informations
     */
    @Override
    public String toString() {
        return "Name : " + IBAN + "           Status : " + (activated ? "activated" : "deactivated") + "          amount : " + getSubAccountList().get(0).getAmount() + " €";
    }

    /**
     * Return whether the account is toggled on or not.
     *
     * @return Whether the account is toggled on or not
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Toggles the account on.
     */
    public void toggleOn() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.put("https://flns-spring-test.herokuapp.com/api/account-access")
                .header("Authorization", "Bearer " + Main.getToken())
                .header("Content-Type", "application/json")
                .body("{\r\n    \"accountId\": \"" + this.IBAN + "\",\r\n    \"userId\": \"" + this.accountCoOwner + "\",\r\n    \"access\": true,\r\n    \"hidden\": " + this.archived + "\r\n}")
                .asString();
        Main.errorCheck(response.getStatus());
        this.activated = true;
    }

    /**
     * Toggles the account off.
     */
    public void toggleOff() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.put("https://flns-spring-test.herokuapp.com/api/account-access")
                .header("Authorization", "Bearer " + Main.getToken())
                .header("Content-Type", "application/json")
                .body("{\r\n    \"accountId\": \"" + this.IBAN + "\",\r\n    \"userId\": \"" + this.accountCoOwner + "\",\r\n    \"access\": false,\r\n    \"hidden\": " + this.archived + "\r\n}")
                .asString();
        Main.errorCheck(response.getStatus());
        this.activated = false;
    }


    public Profile getAccountOwner() {
        return this.accountOwner;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    public ArrayList<Profile> getAccountCoOwner() {
        return this.accountCoOwner;
    }

    public String getIBAN() {
        return this.IBAN;
    }

    public boolean isArchived() {
        return this.archived;
    }

    public Bank getBank() {
        return this.bank;
    }

    public ArrayList<SubAccount> getSubAccountList() {
        return this.subAccountList;
    }

    public boolean canPay() {
        return canPay;
    }

    // TODO : remove
    public double getAmount() throws SizeLimitExceededException {
        if (subAccountList.size() != 1)
            throw new SizeLimitExceededException("There should only be one sub account in account " + IBAN);
        return subAccountList.get(0).getAmount();
    }

    // TODO : remove
    public double getAmountDaysAgo(int days) throws SizeLimitExceededException {
        if (subAccountList.size() != 1)
            throw new SizeLimitExceededException("There should only be one sub account in account " + IBAN);
        double amount = 0;
        SubAccount account = subAccountList.get(0);
        ArrayList<Transaction> history = account.getTransactionHistory();
        for (Transaction t : history) {
            System.out.println(t.getSendingDate());
        }
        return 0;
    }
}
