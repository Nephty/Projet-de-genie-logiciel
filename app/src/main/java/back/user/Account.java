package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;

/**
 * @author François VION
 */
public class Account {
    private final Profile accountOwner;
    private final ArrayList<Profile> accountCoOwner;
    private final Bank bank;
    private final String IBAN;
    private final AccountType accountType;
    private boolean archived;
    private final boolean canPay;
    private final ArrayList<SubAccount> subAccountList;
    private boolean activated;
    private int numberOfSubAccounts;


    /**
     * Creates an Account object with all the information needed
     *
     * @param accountOwner   The owner of the account in a Profile object
     * @param accountCoOwner A co owner of the account in a Profile object
     * @param bank           The bank where the account is. In a Bank object
     * @param IBAN           A String of the IBAN
     * @param accountType    The type of the account in accountType enumeration
     * @param activated      A boolean for the activated/deactivated option
     * @param archived       A boolean for archived/unarchived option
     * @param canPay         A boolean for canPay/cannotPay option
     */
    public Account(Profile accountOwner, Profile accountCoOwner, Bank bank, String IBAN, AccountType accountType, boolean activated, boolean archived, boolean canPay){
        this.accountOwner = accountOwner;
        this.accountCoOwner = new ArrayList<>();
        this.accountCoOwner.add(accountCoOwner);
        this.bank = bank;
        this.IBAN = IBAN;
        this.accountType = accountType;
        this.activated = activated;
        this.archived = archived;
        this.canPay = canPay;
        this.subAccountList = new ArrayList<>();
        this.subAccountList.add(new SubAccount(this.IBAN, Currencies.EUR));
        this.numberOfSubAccounts = this.subAccountList.size();
    }

    /**
     * @return A String to display the account information
     */
    @Override
    public String toString() {
        return "Bank : " + this.getBank().getSwiftCode()+ " " + this.IBAN + "  amount : " + getSubAccountList().get(0).getAmount() + " €";
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
     * Toggles the account.
     */
    public void toggle(){
        this.activated = !this.activated;
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.put("https://flns-spring-test.herokuapp.com/api/account-access")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"accountId\": \"" + this.IBAN + "\",\r\n    \"userId\": \"" + this.accountOwner.getNationalRegistrationNumber() + "\",\r\n    \"access\": true,\r\n    \"hidden\": " + (!this.activated) + "\r\n}")
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
        Main.errorCheck(response.getStatus());
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

    public void setArchived(boolean value) {
        archived = value;
    }
}
