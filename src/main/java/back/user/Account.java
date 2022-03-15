package back.user;

import java.util.ArrayList;

public class Account {
    private Profile accountOwner;
    private ArrayList<Profile> accountCoOwner;
    private ArrayList<Transaction> transactionHistory;
    private final String IBAN;
    private boolean activated;
    private boolean archived;
    private Bank bank;
    private ArrayList<SubAccount> subAccountList;

    public Account(String IBAN) {
        this.IBAN = IBAN;
        // TODO : Instancier les variables via l'API avec l'IBAN
    }

    @Override
    public String toString() {
        return "Name : " + IBAN + "           Status : " + (activated ? "activated" : "deactivated");
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


    public void exportHistory() {
        // TODO : Expoter l'historique
    }

    public Profile getAccountOwner() {
        return this.accountOwner;
    }

    public ArrayList<Profile> getAccountCoOwner() {
        return this.accountCoOwner;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return this.transactionHistory;
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
