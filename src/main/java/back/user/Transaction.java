package back.user;

import app.Main;

public class Transaction {
    private long ID;
    private String senderName;
    private String senderIBAN;
    private String receiverName;
    private String receiverIBAN;
    private double amount;
    private String sendingDate;
    private Currencies currency;

    public Transaction(long ID, String senderName, String senderIBAN, String receiverName, String receiverIBAN, double amount, String sendingDate, Currencies currency){
        this.ID = ID;
        this.senderName = senderName;
        this.senderIBAN = senderIBAN;
        this.receiverName = receiverName;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.sendingDate = sendingDate;
        this.currency = currency;
    }

    @Override
    public String toString(){
        // If the transaction is sent by the account owner
        if(this.senderIBAN.equals(Main.getCurrentAccount().getIBAN())){
            return this.sendingDate+"      "+this.receiverName + "      " + this.receiverIBAN + "       -"+this.amount+"€";
        } else{
            return this.sendingDate+"      "+this.senderName+ "      " + this.senderIBAN + "       +"+this.amount+"€";
        }
    }

    public String getSenderName() {
        return this.senderName;
    }

    public String getSenderIBAN() {
        return this.senderIBAN;
    }

    public String getReceiverName() {
        return this.senderName;
    }

    public String getReceiverIBAN() {
        return this.senderName;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getSendingDate() {
        return this.sendingDate;
    }

    public long getID() {
        return this.ID;
    }

    public Currencies getCurrency() {
        return this.currency;
    }
}
