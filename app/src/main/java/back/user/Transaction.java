package back.user;

import app.Main;

public class Transaction {
    private final long ID;
    private final String senderName;
    private final String senderIBAN;
    private final String receiverName;
    private final String receiverIBAN;
    private final double amount;
    private final String sendingDate;
    private final Currencies currency;

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
