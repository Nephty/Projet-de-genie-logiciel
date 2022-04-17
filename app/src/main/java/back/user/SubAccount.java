package back.user;



public class SubAccount {
    private final String IBAN;
    private final Currencies currency;
//    private final double amount; // TODO : A retirer ?

    /**
     * Creates a SubAccount object with an HTTP request by using the IBAN and the currency of an account
     *
     * @param IBAN     The String og the IBAN
     * @param currency The currency
     */
    public SubAccount(String IBAN, Currencies currency) {
        this.IBAN = IBAN;
        this.currency = currency;
    }

    public Currencies getCurrency() {
        return this.currency;
    }
}
