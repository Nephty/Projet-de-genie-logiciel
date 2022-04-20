package back.user;

/**
 * This class represent a communication
 */
public abstract class Communication {
    protected Profile client;
    protected Bank bank;

    protected Bank getBank() {
        return this.bank;
    }

}