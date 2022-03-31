package back.user;

/**
 * This class represent a communication
 */
public abstract class Communication {
    protected Profile client;
    protected Bank bank;
    protected Wallet wallet;

    protected Profile getClient() {
        return this.client;
    }

    protected Bank getBank() {
        return this.bank;
    }

}