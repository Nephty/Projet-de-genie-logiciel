package back.user;

import java.util.Currency;

public class Transaction {
    private int id;
    private String sender;
    private String beneficiary;
    private float amount;
    private Currency currency;

    public Transaction(int id, String sender, String beneficiary, float amount, Currency currency) {
        this.id = id;
        this.sender = sender;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ID : " + id + " | Sender : " + sender + " | Beneficiary : " + beneficiary + " | Amount : " + amount + " " + currency.getCurrencyCode();
    }
}
