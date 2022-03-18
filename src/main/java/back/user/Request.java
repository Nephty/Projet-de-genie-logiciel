package back.user;

public class Request extends Communication {
    private final Reason reason;

    /**
     * Creates a request with all the informations needed
     * @param client The Profile object of a user
     * @param bank The Bank object of the destinated bank
     * @param reason The reason of the request
     */
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
