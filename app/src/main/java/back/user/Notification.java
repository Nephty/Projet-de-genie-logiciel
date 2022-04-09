package back.user;

public class Notification extends Communication {
    private final String content;
    private String senderName;
    private String date;
    private boolean flag;
    private long ID;

    public Notification(String senderName, String content, String date, long ID, boolean flag) {
        this.content = content;
        this.senderName = senderName;
        this.date = date;
        this.ID = ID;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return this.date + "      " + this.senderName + "     " + this.content;
    }

    public String getContent() {
        return this.content;
    }

    public String getSenderName() {
        return this.senderName;
    }
}