package back.user;

public class Request extends Communication{
    private Reason reason;

    public Request(Profile client, Bank bank, Reason reason){
        this.client = client;
        this.bank = bank;
        this.reason = reason;
    }

    public Reason getReason(){
        return this.reason;
    }
}
