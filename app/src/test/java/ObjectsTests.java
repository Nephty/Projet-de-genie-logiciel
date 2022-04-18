import app.Main;
import back.user.Portfolio;
import back.user.Profile;
import back.user.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author François VION
 */
public class ObjectsTests {

    @Test
    @DisplayName("Wallet creation with deleted account")
    public void createWallets1(){
        Profile user = new Profile("Carlos","Matos", "Matos01020312300", "English", "01.02.03-123.00");
        Main.setUser(user);
        Portfolio testPortfolio = new Portfolio(user);        String body = "";
        String body2 = "{\"accountId\":\"BE00000000000071\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE00000000000071\",\"swift\":\"ABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-10\",\"deleted\":true}}\n";
        String body3 = "";
        ArrayList<Wallet> testWalletList = testPortfolio.createWallets(body, body2, body3);
        assertEquals(testWalletList.size(), 1);
        assertEquals(testWalletList.get(0).getBank().getSwiftCode(), "ABCD");
        assertEquals(testWalletList.get(0).getAccountUser().getNationalRegistrationNumber(), "01.02.03-123.00");
        assertEquals(testWalletList.get(0).getAccountList().size(), 0);
    }

    @Test
    @DisplayName("Wallet creation with a desactivated account")
    public void createWallets2(){
        Profile user = new Profile("Carlos","Matos", "Matos01020312300", "English", "01.02.03-123.00");
        Main.setUser(user);
        Portfolio testPortfolio = new Portfolio(user);
        String body = "";
        String body2 = "";
        String body3 = "{\"accountId\":\"BE00000000000071\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":true,\"account\":{\"iban\":\"BE00000000000071\",\"swift\":\"ABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-10\",\"deleted\":false}}\n";
        ArrayList<Wallet> testWalletList = testPortfolio.createWallets(body, body2, body3);
        assertEquals(testWalletList.size(), 1);
        assertEquals(testWalletList.get(0).getBank().getSwiftCode(), "ABCD");
        assertEquals(testWalletList.get(0).getAccountUser().getNationalRegistrationNumber(), "01.02.03-123.00");
        assertEquals(testWalletList.get(0).getAccountList().size(), 0);
    }

    @Test
    @DisplayName("JSON array parser")
    public void JSONArrayParser(){
        String toParse = "{\"jsonNumber\": 1},{\"jsonNumber\": 2},{\"jsonNumber\": 3}";
        ArrayList<String> parsedList = Portfolio.JSONArrayParser(toParse);
        assertEquals(parsedList.size(), 3);
        assertEquals(parsedList.get(0), "{\"jsonNumber\": 1}");
        assertEquals(parsedList.get(1), "{\"jsonNumber\": 2}");
        assertEquals(parsedList.get(2), "{\"jsonNumber\": 3}");
    }


    @Test
    @DisplayName("Wallet creation with normal accounts")
    public void createWallets3(){
        Profile user = new Profile("Carlos","Matos", "Matos01020312300", "English", "01.02.03-123.00");
        Main.setUser(user);
        Portfolio testPortfolio = new Portfolio(user);
        String body = "{\"accountId\":\"BE00000000000071\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE00000000000071\",\"swift\":\"ABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-10\",\"deleted\":false}}\n";
        String body2 = "";
        String body3 = "";
        ArrayList<Wallet> testWalletList = testPortfolio.createWallets(body, body2, body3);
        assertEquals(testWalletList.size(), 1);
        assertEquals(testWalletList.get(0).getBank().getSwiftCode(), "ABCD");
        assertEquals(testWalletList.get(0).getAccountUser().getNationalRegistrationNumber(), "01.02.03-123.00");
        assertEquals(testWalletList.get(0).getAccountList().size(), 1);
        assertEquals(testWalletList.get(0).getAccountList().get(0).getIBAN(), "BE00000000000071");
    }
}
