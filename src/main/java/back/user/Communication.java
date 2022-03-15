package back.user;

public abstract class Communication {
    protected Profile client;
    protected Bank bank;

    protected Profile getClient() {
        return this.client;
    }

    protected Bank getBank() {
        return this.bank;
    }

}