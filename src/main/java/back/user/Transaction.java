package back.user;

public class Transaction {
    private String senderName;
    private String senderIBAN;
    private String receiverName;
    private String receiverIBAN;
    private double amount;
    private String communication;
    private String sendingDate;
    private long ID;
    private Currencies currency;

    public Transaction(long ID){
        this.ID = ID;
        // TODO : Donne une valeur a toutes les variables via l'API grâce à l'ID
    }

    public String getSenderName(){
        return this.senderName;
    }

    public String getSenderIBAN(){
        return this.senderIBAN;
    }

    public String getReceiverName(){
        return this.senderName;
    }

    public String getReceiverIBAN(){
        return this.senderName;
    }

    public double getAmount(){
        return this.amount;
    }

    public String getCommunication(){
        return this.communication;
    }

    public String getSendingDate(){
        return this.sendingDate;
    }

    public long getID(){
        return this.ID;
    }

    public Currencies getCurrency() {
        return this.currency;
    }
}
