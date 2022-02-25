package back.user;

public class Request {
    public String name;

    public Request(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
