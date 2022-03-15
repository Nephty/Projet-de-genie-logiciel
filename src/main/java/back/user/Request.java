package back.user;

public class Request extends Communication {
    private final Reason reason;

    public Request(Profile client, Bank bank, Reason reason) {
        this.client = client;
        this.bank = bank;
        this.reason = reason;
    }

    public void send() {
        // TODO : Envoyer la requête à l'API
    }

    public Reason getReason() {
        return this.reason;
    }
}
