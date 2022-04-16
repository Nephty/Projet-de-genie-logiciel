package back.user;

/**
 * This class represent a communication
 */
public abstract class Communication {
    protected Bank bank;
    protected Wallet wallet;

    protected Bank getBank() {
        return this.bank;
    }

}