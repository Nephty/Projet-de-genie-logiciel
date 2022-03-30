package back.user;

public class Request extends Communication {
    private final CommunicationType communicationType;

    /**
     * Creates a request with all the informations needed
     *
     * @param client The Profile object of a user
     * @param bank   The Bank object of the destinated bank
     * @param communicationType The communicationType of the request
     */
    public Request(Profile client, Bank bank, CommunicationType communicationType) {
        this.client = client;
        this.bank = bank;
        this.communicationType = communicationType;
    }

    public void send() {
        // TODO : Send the request to the API
    }

    public CommunicationType getReason() {
        return this.communicationType;
    }
}
