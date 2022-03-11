package client.back.user;

import java.util.ArrayList;

public class Portfolio {
    private Profile user;
    private ArrayList<Wallet> walletList;

    public Portfolio(String nationalRegistrationNumber){
        //Crée la liste des wallets via l'API grâce au numéro national
    }

    public Profile getUser(){
        return this.user;
    }

    public ArrayList<Wallet> getWalletList(){
        return this.walletList;
    }
}
