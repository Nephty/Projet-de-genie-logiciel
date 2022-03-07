package back.user;

public class Notification {
    public String name;

    public Notification(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
