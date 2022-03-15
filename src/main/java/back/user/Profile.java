package back.user;

public class Profile {
    private final String firstName;
    private final String lastName;
    private final String nationalRegistrationNumber;
    private final Portfolio portfolio;

    public Profile(String firstName, String lastName, String nationalRegistrationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalRegistrationNumber = nationalRegistrationNumber;
        this.portfolio = new Portfolio(nationalRegistrationNumber);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getNationalRegistrationNumber() {
        return this.nationalRegistrationNumber;
    }

    public Portfolio getPortfolio() {
        return this.portfolio;
    }
}
