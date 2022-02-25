package back.user;

public class Wallet {
    String name;

    public Wallet(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
