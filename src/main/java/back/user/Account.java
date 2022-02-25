package back.user;

public class Account {
    public String name;

    public Account(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
