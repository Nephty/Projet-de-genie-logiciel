package back.user;

public class Notification extends Communication {
    private final String content;
    private String senderName;
    private String date;
    private boolean flag;
    private boolean dismiss;

    public Notification(String senderName, String content, String date) {
        this.content = content;
        this.senderName = senderName;
        this.date = date;
        this.flag = flag;
        this.dismiss = dismiss;
    }

    @Override
    public String toString(){
        return this.date + "      " + this.senderName + "     " + this.content;
    }

    public String getContent() {
        return this.content;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void dismiss() {
        this.dismiss = true;
        // TODO : DELETE Notification
    }

    public void changeFlag() {
        // TODO : PUT Notification
        if(flag){
            this.flag = false;
        } else{
            this.flag = true;
        }
    }
}
