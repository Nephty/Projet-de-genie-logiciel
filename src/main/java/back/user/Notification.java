package back.user;

public class Notification extends Communication {
    private final String content;
    public String name;
    private boolean flag;
    private boolean dismiss;

    public Notification(Bank bank, Profile profile, String name, String content) {
        this.client = profile;
        this.bank = bank;
        this.content = content;
        this.name = name;
        this.flag = false;
        this.dismiss = false;
    }

    public String getContent() {
        return this.content;
    }

    public String getName() {
        return this.name;
    }

    public void setDismiss(boolean value) {
        this.dismiss = value;
    }

    public void setFlag(boolean value) {
        this.flag = value;
    }
}
