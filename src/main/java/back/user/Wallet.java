package back.user;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;

public class Wallet {
    private ArrayList<Account> accountList;
    private Profile accountUser;
    private Bank bank;

    public Wallet(Profile accountUser, Bank bank, ArrayList<Account> accountList) throws UnirestException {
        this.accountUser = accountUser;
        this.bank = bank;
        this.accountList = accountList;
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
