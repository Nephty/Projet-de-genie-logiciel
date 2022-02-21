package back.user;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String lastName, firstName, NRN, email, username;
    private static User instance;

    private User(String lastName, String firstName, String NRN, String email, String username) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.NRN = NRN;
        this.email = email;
        this.username = username;
    }

    private User() {
    }

    public static User getInstance() {
        if (instance == null) return new User();
        return instance;
    }

    public static User getInstance(String lastName, String firstName, String NRN, String email, String username) {
        return new User(lastName, firstName, NRN, email, username);
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public String toString() {
        String res = "";
        int maxLength = 0;
        for (String s : new ArrayList<>(Arrays.asList(lastName, firstName, NRN, email, username))) {
            if (s.length() > maxLength) maxLength = s.length();
        }
        for (int i = 0; i < maxLength + 17; i++) {
            res += "-";
        }
        res += "\n";
        res += "| Last name  | " + lastName;
        for (int i = 0; i < maxLength - lastName.length(); i++) {
            res += " ";
        }
        res += " |\n";
        res += "| First name | " + firstName;
        for (int i = 0; i < maxLength - firstName.length(); i++) {
            res += " ";
        }
        res += " |\n";
        res += "| NRN        | " + NRN;
        for (int i = 0; i < maxLength - NRN.length(); i++) {
            res += " ";
        }
        res += " |\n";
        res += "| Email      | " + email;
        for (int i = 0; i < maxLength - email.length(); i++) {
            res += " ";
        }
        res += " |\n";
        res += "| Username   | " + username;
        for (int i = 0; i < maxLength - username.length(); i++) {
            res += " ";
        }
        res += " |\n";
        for (int i = 0; i < maxLength + 17; i++) {
            res += "-";
        }
        res += "\n";
        return res;
    }
}
