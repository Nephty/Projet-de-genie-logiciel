package back.user;

import java.time.Instant;

public class Transaction {
    private long ID;
    private String senderName;
    private String senderIBAN;
    private String receiverName;
    private String receiverIBAN;
    private double amount;
    private String sendingDate;
    private Currencies currency;

    /**
     * Creates the Transaction object byt giving all the needed informations
     *
     * @param ID           The ID of the transaction
     * @param senderName   The String of the senderName
     * @param senderIBAN   The String of the sender IBAN
     * @param receiverName The String of the receiver name
     * @param receiverIBAN The String of the receiver IBAN
     * @param amount       The amount of the transaction
     * @param sendingDate  The String of the sending date
     * @param currency     The int corresponding to the type of currency
     */
    public Transaction(long ID, String senderName, String senderIBAN, String receiverName, String receiverIBAN, double amount, String sendingDate, Currencies currency) {
        this.ID = ID;
        this.senderName = senderName;
        this.senderIBAN = senderIBAN;
        this.receiverName = receiverName;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.sendingDate = sendingDate;
        this.currency = currency;
    }


    /**
     * @return A String to display the transaction informations
     */
    @Override
    public String toString() {
        // If the transaction is sent by the account owner
        if (this.senderIBAN.equals("BE327777888877778888")) { // TODO : reset this to Main.getCurrentAccount().getIBAN()
            return this.sendingDate + "      " + this.receiverName + "      " + this.receiverIBAN + "       -" + this.amount + "€";
        } else {
            return this.sendingDate + "      " + this.senderName + "      " + this.senderIBAN + "       +" + this.amount + "€";
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
        return this.senderIBAN;
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

    public long getSendingDateAsDateObject() {
        // TODO : make this method return the date of the transaction as ms since epoch
        return Instant.now().toEpochMilli();
    }
}
