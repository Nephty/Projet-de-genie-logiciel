package back.user;

public class Request extends Communication {
    private final CommunicationType communicationType;

    /**
     * Creates a request with all the required information
     *
     * @param client            The Profile object of a user
     * @param bank              The Bank object of the destinated bank
     * @param communicationType The communicationType of the request
     */
    public Request(Profile client, Bank bank, CommunicationType communicationType) {
        this.client = client;
        this.bank = bank;
        this.communicationType = communicationType;
        this.wallet = null;
    }

    /**
     * Creates a request with all the required information
     *
     * @param client            The Profile object of a user
     * @param wallet            The Wallet object of the destinated bank
     * @param communicationType The communicationType of the request
     */
    public Request(Profile client, Wallet wallet, CommunicationType communicationType) {
        this.client = client;
        this.wallet = wallet;
        this.communicationType = communicationType;
        this.bank = null;
    }

    public void send() {
        // TODO : Send the request to the API
    }

    public CommunicationType getReason() {
        return communicationType;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
