package back.user;

public class Notification extends Communication {
    public String name;
    private final String content;
    private boolean flag;
    private boolean dismiss;


    /**
     * Creates the Notification with all the informations needed
     * @param bank
     * @param profile
     * @param name
     * @param content
     */
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

    public void setDismiss(boolean value){
        this.dismiss = value;
    }

    public void setFlag(boolean value){
        this.flag = value;
    }
}