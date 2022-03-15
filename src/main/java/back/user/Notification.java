package back.user;

public class Notification extends Communication {
    public String name;
    private final String content;

    // TODO : Implémenter toutes les valeurs comme dans la BDD (dismiss etc)

    public Notification(Bank bank, Profile profile, String name, String content) {
        this.client = profile;
        this.bank = bank;
        this.content = content;
        this.name = name;
    }

    public String getContent() {
        return this.content;
    }

    public String getName() {
        return this.name;
    }
}
