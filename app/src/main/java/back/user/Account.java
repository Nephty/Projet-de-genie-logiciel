package back.user;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;

public class Account {
    private final Profile accountOwner;
    private final Profile accountCoOwner;
    private final Bank bank;
    private final String IBAN;
    private final AccountType accountType;
    private final boolean archived;
    private final boolean canPay;
    private final ArrayList<SubAccount> subAccountList;
    private boolean activated;


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
    public Account(Profile accountOwner, Profile accountCoOwner, Bank bank, String IBAN, AccountType accountType, boolean activated, boolean archived, boolean canPay) {
        this.accountOwner = accountOwner;
        this.accountCoOwner = accountCoOwner;
        this.bank = bank;
        this.IBAN = IBAN;
        this.accountType = accountType;
        this.activated = activated;
        this.archived = archived;
        this.canPay = canPay;
        this.subAccountList = new ArrayList<SubAccount>();
        this.subAccountList.add(new SubAccount(this.IBAN, Currencies.EUR));
    }

    public Account(Profile accountOwner, Profile accountCoOwner, Bank bank, String IBAN, AccountType accountType, boolean activated, boolean archived, boolean canPay, double amount) {
        this.accountOwner = accountOwner;
        this.accountCoOwner = accountCoOwner;
        this.bank = bank;
        this.IBAN = IBAN;
        this.accountType = accountType;
        this.activated = activated;
        this.archived = archived;
        this.canPay = canPay;
        this.subAccountList = new ArrayList<SubAccount>();
        this.subAccountList.add(new SubAccount(this.IBAN, Currencies.EUR, amount));
    }

        /**
         * @return A String to display account informations
         */
//    @Override
//    public String toString() {
//        return "Name : " + IBAN + "           Status : " + (activated ? "activated" : "deactivated") + "          amount : " + getSubAccountList().get(0).getAmount() + " €";
//    }


    /**
     * Return whether the account is toggled on or not.
     *
     * @return Whether the account is toggled on or not
     */
    public boolean isActivated() {
        return activated;
    }


    public boolean canPay() {
        return canPay;
    }

    public void delete() {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.delete("https://flns-spring-test.herokuapp.com/api/account/" + this.IBAN)
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
    }

    public Profile getAccountOwner() {
        return this.accountOwner;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    public Profile getAccountCoOwner() {
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
}