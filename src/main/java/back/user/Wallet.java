package back.user;

import java.util.ArrayList;

public class Wallet {
    private final Profile accountUser;
    private ArrayList<Account> accountList;
    private Bank bank;

    public Wallet(Profile accountUser) {
        this.accountUser = accountUser;
        // TODO : Crée la liste des comptes d'un wallet via l'api grâce au Profile
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
}
