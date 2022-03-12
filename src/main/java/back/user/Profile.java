package back.user;

public class Profile {
    private String firstName;
    private String lastName;
    private String nationalRegistrationNumber;
    private Portfolio portfolio;

    public Profile(String nationalRegistrationNumber){

        this.nationalRegistrationNumber = nationalRegistrationNumber;
        this.portfolio = new Portfolio(nationalRegistrationNumber);
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getNationalRegistrationNumber(){
        return this.nationalRegistrationNumber;
    }

    public Portfolio getPortfolio(){
        return this.portfolio;
    }
}
