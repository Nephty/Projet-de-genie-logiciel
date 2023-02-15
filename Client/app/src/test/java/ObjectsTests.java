import app.Main;
import back.user.*;
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
    public void createWallets1() {
        Profile user = new Profile("Carlos", "Matos", "Matos01020312300", "English", "01.02.03-123.00");
        Main.setUser(user);
        Portfolio testPortfolio = new Portfolio(user);
        String body = "";
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
    public void createWallets2() {
        Profile user = new Profile("Carlos", "Matos", "Matos01020312300", "English", "01.02.03-123.00");
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
    public void JSONArrayParser() {
        String toParse = "{\"jsonNumber\": 1},{\"jsonNumber\": 2},{\"jsonNumber\": 3}";
        ArrayList<String> parsedList = Portfolio.JSONArrayParser(toParse);
        assertEquals(parsedList.size(), 3);
        assertEquals(parsedList.get(0), "{\"jsonNumber\": 1}");
        assertEquals(parsedList.get(1), "{\"jsonNumber\": 2}");
        assertEquals(parsedList.get(2), "{\"jsonNumber\": 3}");
    }

    @Test
    @DisplayName("JSON Swift parser")
    public void parseSwift() {
        String json = "[{\"swift\":\"testSwift\",\"name\":\"testName\",\"password\":\"$2a$10$LhuiLXrd0hC/MxKp34B88em4zfeikubLGQ7EyJE0ah3FhociNvL5K\",\"address\":\"testAddress\",\"country\":\"UK\",\"defaultCurrencyType\":{\"currencyId\":0,\"currencyTypeName\":\"EUR\"}},{\"swift\":\"GEBABEBB\",\"name\":\"BNP\",\"password\":\"$2a$10$caWNFLjz7XeamSnP81RP..MsBaAaYl3eqR2Xtlai23p.40gPuHNHC\",\"address\":\"Bruxelles\",\"country\":\"Belgique\",\"defaultCurrencyType\":{\"currencyId\":0,\"currencyTypeName\":\"EUR\"}},{\"swift\":\"BEGLGLGL\",\"name\":\"UwU\",\"password\":\"$2a$10$HSD11g0a3v9WQpLduAkSIuiGTLqpNHUd6FNwm4IsSLgMRELbfXPIq\",\"address\":\"Tournai\",\"country\":\"Mons\",\"defaultCurrencyType\":{\"currencyId\":0,\"currencyTypeName\":\"EUR\"}},{\"swift\":\"ABCDABCD\",\"name\":\"Belfius\",\"password\":\"$2a$10$SCDwUwZf4EGOoT.eIMxdGOGoIGQSHVjrm7xeHLJGwqlSW4i2cUzV6\",\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyType\":{\"currencyId\":0,\"currencyTypeName\":\"EUR\"}}]";
        ArrayList<String> swiftList = Bank.parseSwift(json);
        assertEquals(swiftList.size(), 4);
        assertEquals(swiftList.get(0), "testSwift");
        assertEquals(swiftList.get(1), "GEBABEBB");
        assertEquals(swiftList.get(2), "BEGLGLGL");
        assertEquals(swiftList.get(3), "ABCDABCD");
    }

    @Test
    @DisplayName("JSON request parser")
    public void parseRequest() {
        String json = "[{\"notificationType\":0,\"comments\":\"\",\"isFlagged\":false,\"recipientId\":\"GEBABEBB\",\"notificationId\":85,\"date\":\"2022-04-22\",\"senderName\":\"Matos Carlos\",\"senderId\":\"01.02.03-123.00\",\"notificationTypeName\":\"CREATE_ACCOUNT\"},{\"notificationType\":4,\"comments\":\"The bank BNP hasn't created you a new account\",\"isFlagged\":false,\"recipientId\":\"01.02.03-123.00\",\"notificationId\":84,\"date\":\"2022-04-20\",\"senderName\":\"BNP\",\"senderId\":\"GEBABEBB\",\"notificationTypeName\":\"CUSTOM\"},{\"notificationType\":2,\"comments\":\"BE01020300000000\",\"isFlagged\":false,\"recipientId\":\"BEGLGLGL\",\"notificationId\":86,\"date\":\"2022-04-23\",\"senderName\":\"Matos Carlos\",\"senderId\":\"01.02.03-123.00\",\"notificationTypeName\":\"TRANSFER_PERMISSION\"}]";
        ArrayList<Request> reqList = Request.parseRequest(json);
        assertEquals(reqList.size(), 2);
        assertEquals(reqList.get(0).getCommunicationType(), CommunicationType.CREATE_ACCOUNT);
        assertEquals(reqList.get(0).getRecipientId(), "GEBABEBB");
        assertEquals(reqList.get(1).getCommunicationType(), CommunicationType.TRANSFER_PERMISSION);
        assertEquals(reqList.get(1).getContent(), "BE01020300000000");

    }

    @Test
    @DisplayName("Transaction history creation")
    public void createHistory() {
        String json = "{\"transactionTypeId\":1,\"senderIban\":\"0123456789ABCDEF\",\"recipientIban\":\"BE01010101010101\",\"currencyId\":0,\"transactionAmount\":1.0,\"transactionDate\":\"2022-04-12\",\"comments\":\"Communication\",\"senderName\":\"Musk Elon\",\"recipientName\":\"Musk Elon\",\"currencyName\":\"EUR\",\"transactionId\":19,\"processed\":true},{\"transactionTypeId\":1,\"senderIban\":\"BE12345678910118\",\"recipientIban\":\"BE01010101010101\",\"currencyId\":0,\"transactionAmount\":20.0,\"transactionDate\":\"2022-04-15\",\"comments\":\"Bonjour\",\"senderName\":\"Moreau Benoit\",\"recipientName\":\"Musk Elon\",\"currencyName\":\"EUR\",\"transactionId\":20,\"processed\":true},{\"transactionTypeId\":1,\"senderIban\":\"BE01010101010101\",\"recipientIban\":\"BE01020300000000\",\"currencyId\":0,\"transactionAmount\":10.0,\"transactionDate\":\"2022-04-22\",\"comments\":\"TestTransaction\",\"senderName\":\"Musk Elon\",\"recipientName\":\"Matos Carlos\",\"currencyName\":\"EUR\",\"transactionId\":39,\"processed\":true}";
        ArrayList<Transaction> transactionList = SubAccount.createHistory(json);
        assertEquals(3, transactionList.size());
        assertEquals(19, transactionList.get(0).getID());
        assertEquals(20.0, transactionList.get(1).getAmount());
        assertEquals("2022-04-22", transactionList.get(2).getSendingDate());
        assertEquals("BE01020300000000", transactionList.get(2).getReceiverIBAN());
        assertEquals("Matos Carlos", transactionList.get(2).getReceiverName());
        assertEquals("Communication", transactionList.get(0).getMessage());
    }


//    @Test
//    @DisplayName("Wallet creation with normal accounts")
//    public void createWallets3(){
//        Profile user = new Profile("Carlos","Matos", "Matos01020312300", "English", "01.02.03-123.00");
//        Main.setUser(user);
//        Portfolio testPortfolio = new Portfolio(user);
//        String body = "{\"accountId\":\"BE00000000000071\",\"userId\":\"01.02.03-123.00\",\"access\":true,\"hidden\":false,\"account\":{\"iban\":\"BE00000000000071\",\"swift\":\"ABCD\",\"userId\":\"01.02.03-123.00\",\"accountTypeId\":1,\"payment\":false,\"ownerFirstname\":\"Carlos\",\"ownerLastname\":\"Matos\",\"linkedBank\":{\"swift\":\"ABCD\",\"name\":\"Belfius\",\"password\":null,\"address\":\"uwuwuwuwu\",\"country\":\"BE\",\"defaultCurrencyId\":0,\"defaultCurrencyName\":\"EUR\"},\"nextProcess\":\"2023-04-10\",\"deleted\":false}}\n";
//        String body2 = "";
//        String body3 = "";
//        ArrayList<Wallet> testWalletList = testPortfolio.createWallets(body, body2, body3);
//        assertEquals(testWalletList.size(), 1);
//        assertEquals(testWalletList.get(0).getBank().getSwiftCode(), "ABCD");
//        assertEquals(testWalletList.get(0).getAccountUser().getNationalRegistrationNumber(), "01.02.03-123.00");
//        assertEquals(testWalletList.get(0).getAccountList().size(), 1);
//        assertEquals(testWalletList.get(0).getAccountList().get(0).getIBAN(), "BE00000000000071");
//    }
}
