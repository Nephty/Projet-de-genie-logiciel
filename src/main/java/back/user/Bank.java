package back.user;

public class Bank {
    private String name;
    private final String swiftCode;

    public Bank(String swiftCode) {
        this.swiftCode = swiftCode;
        //Donne la valeur à name via l'API grâce au swiftCode
    }

    public String getName() {
        return this.name;
    }

    public String getSwiftCode() {
        return this.swiftCode;
    }
}
