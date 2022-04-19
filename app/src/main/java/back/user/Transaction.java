package back.user;

import app.Main;

import java.sql.Date;

/**
 * @author François VION
 */
public class Transaction {
    private final long ID;
    private final String senderName;
    private final String senderIBAN;
    private final String receiverName;
    private final String receiverIBAN;
    private final double amount;
    private final String sendingDate;
    private final Currencies currency;
    private final String message;

    /**
     * Creates the Transaction object byt giving all the needed information
     *
     * @param ID           The ID of the transaction
     * @param senderName   The String of the senderName
     * @param senderIBAN   The String of the sender IBAN
     * @param receiverName The String of the receiver name
     * @param receiverIBAN The String of the receiver IBAN
     * @param amount       The amount of the transaction
     * @param sendingDate  The String of the sending date
     * @param currency     The int corresponding to the type of currency
     * @param message      The String of the message
     */
    public Transaction(long ID, String senderName, String senderIBAN, String receiverName, String receiverIBAN, double amount, String sendingDate, Currencies currency, String message) {
        this.ID = ID;
        this.senderName = senderName;
        this.senderIBAN = senderIBAN;
        this.receiverName = receiverName;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.sendingDate = sendingDate;
        this.currency = currency;
        this.message = message;
    }


    /**
     * @return A String to display the transaction information
     */
    @Override
    public String toString() {
        // If the transaction is sent by the account owner
        if (this.senderIBAN.equals(Main.getCurrentAccount().getIBAN())) {
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
        return this.receiverName;
    }

    public String getReceiverIBAN() {
        return this.receiverIBAN;
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

    public Date getSendingDateAsDateObject() {
        return Date.valueOf(sendingDate);
    }

    public String getMessage(){
        return this.message;
    }
}
