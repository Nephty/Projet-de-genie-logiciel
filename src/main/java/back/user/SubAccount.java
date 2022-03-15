package back.user;

import java.util.ArrayList;

public class SubAccount {
    private final String IBAN;
    private final Currencies currency;
    private double amount;
    private ArrayList<Transaction> transactionHistory;


    public SubAccount(String IBAN, Currencies currency) {
        this.IBAN = IBAN;
        this.currency = currency;
        // TODO : Instancier les valeurs grâce à l'IBAN et la Currency
        // TODO : Requetes transactions
    }

    public Currencies getCurrency() {
        return this.currency;
    }

    public double amount() {
        return this.amount;
    }
}
