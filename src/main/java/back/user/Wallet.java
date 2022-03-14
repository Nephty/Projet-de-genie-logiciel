package back.user;

import java.util.ArrayList;

public class Wallet {
    private ArrayList<Account> accountList;
    private Profile accountUser;
    private Bank bank;

    public Wallet(Profile accountUser, Bank bank, ArrayList<String> IbanList){
        this.accountUser = accountUser;
        // TODO : Crée la liste des comptes d'un wallet via l'api grâce à la liste d'iban
    }

    public ArrayList<Account> getAccountList(){
        return this.accountList;
    }

    public Profile getAccountUser(){
        return this.accountUser;
    }

    public Bank getBank(){
        return this.bank;
    }
}
