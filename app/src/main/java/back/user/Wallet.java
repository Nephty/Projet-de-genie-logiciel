package back.user;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;

public class Wallet {
    private final ArrayList<Account> accountList;
    private final Profile accountUser;
    private final Bank bank;
    private int numberOfAccounts = 0;

    /**
     * Creates a Wallet object with all the needed information
     *
     * @param accountUser The Profile object of the user
     * @param bank        The Bank object of the bank
     * @param accountList The list of account
     * @throws UnirestException
     */
    public Wallet(Profile accountUser, Bank bank, ArrayList<Account> accountList) throws UnirestException {
        this.accountUser = accountUser;
        this.bank = bank;
        this.accountList = accountList;
        this.numberOfAccounts = accountList.size();
    }

    /**
     * @return A String to display Wallet information
     */
    @Override
    public String toString() {
        String espace = "                    ";
        espace.substring(0, espace.length() - this.bank.getName().length());

        return "Bank : " + this.bank.getName() + espace + "SWIFT : " + this.bank.getSwiftCode();
    }

    public ArrayList<Account> getAccountList() {
        return this.accountList;
    }

    public Profile getAccountUser() {
        return this.accountUser;
    }

    public Bank getBank() {
        return this.bank;
    }

    public int getNumberOfAccounts() {
        this.numberOfAccounts = accountList.size();
        return numberOfAccounts;
    }
}
