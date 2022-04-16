import back.user.Portfolio;
import back.user.Profile;
import back.user.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectsTests {

    @Test
    @DisplayName("Wallet creation with desactivated account")
    public void createWallets(){
        Portfolio testPortfolio = new Portfolio(new Profile("Carlos","Matos", "Matos01020312300", "English", "01.02.03-123.00"));
        String body = "";
        String body2 = "{\"accountId\":\"BE00000000000071\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE00000000000071\",\"swift\":\"ABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-10\",\"deleted\":true}}\n";
        String body3 = "";
        ArrayList<Wallet> testWalletList = testPortfolio.createWallets(body, body2, body3);
        assertEquals(testWalletList.size(), 1);
        assertEquals(testWalletList.get(0).getBank().getSwiftCode(), "ABCD");
        assertEquals(testWalletList.get(0).getAccountUser().getNationalRegistrationNumber(), "01.02.03-123.00");
        assertEquals(testWalletList.get(0).getAccountList().size(), 0);
    }
}
