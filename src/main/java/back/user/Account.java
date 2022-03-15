package back.user;

import java.util.ArrayList;

public class Account {
    private Profile accountOwner;
    private ArrayList<Profile> accountCoOwner;
    private Bank bank;
    private String IBAN;
    private AccountType accountType;
    private boolean activated;
    private boolean archived;
    private boolean canPay;
    private ArrayList<SubAccount> subAccountList;

    public Account(Profile accountOwner, Profile accountCoOwner, Bank bank, String IBAN, AccountType accountType, boolean activated, boolean archived, boolean canPay){
        this.accountOwner = accountOwner;
        this.accountCoOwner = new ArrayList<Profile>();
        this.accountCoOwner.add(accountCoOwner);
        this.bank = bank;
        this.IBAN = IBAN;
        this.accountType = accountType;
        this.activated = activated;
        this.archived = archived;
        this. canPay = canPay;
        // TODO : Requete sub account
    }

    @Override
    public String toString() {
        return "Name : " + IBAN + "           Status : " + (activated ? "activated" : "deactivated");
    }

    /**
     * Return whether the account is toggled on or not.
     * @return Whether the account is toggled on or not
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Toggles the account on.
     */
    public void toggleOn() {
        // TODO : back-end : toggle product on in database
        activated = true;
    }

    /**
     * Toggles the account off.
     */
    public void toggleOff() {
        // TODO : back-end : toggle product off in database
        activated = false;
    }


    public void exportHistory(){
        // TODO : Expoter l'historique
    }

    public Profile getAccountOwner(){
        return this.accountOwner;
    }

    public AccountType getAccountType(){
        return this.accountType;
    }

    public ArrayList<Profile> getAccountCoOwner(){
        return this.accountCoOwner;
    }

    public String getIBAN(){
        return this.IBAN;
    }

    public boolean isArchived(){
        return this.archived;
    }

    public Bank getBank(){
        return this.bank;
    }

    public ArrayList<SubAccount> getSubAccountList(){
        return this.subAccountList;
    }
}
