package back.user;

public class SubAccount {
    private String IBAN;
    private Currencies currency;
    private double amount;

    public SubAccount(String IBAN, Currencies currency){
        this.IBAN = IBAN;
        this.currency = currency;
        // TODO : Instancier les valeurs grâce à l'IBAN et la Currency
    }

    public Currencies getCurrency(){
        return this.currency;
    }

    public double amount(){
        return this.amount;
    }
}
