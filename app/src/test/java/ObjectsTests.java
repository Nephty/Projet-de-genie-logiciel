import app.Main;
import back.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectsTests {

    @Test
    @DisplayName("JSON array parser")
    public void JSONArrayParser() {
        String toParse = "{\"jsonNumber\": 1},{\"jsonNumber\": 2},{\"jsonNumber\": 3}";
        ArrayList<String> parsedList = Bank.JSONArrayParser(toParse);
        assertEquals(parsedList.size(), 3);
        assertEquals(parsedList.get(0), "{\"jsonNumber\": 1}");
        assertEquals(parsedList.get(1), "{\"jsonNumber\": 2}");
        assertEquals(parsedList.get(2), "{\"jsonNumber\": 3}");
    }

    @Test
    @DisplayName("Parse customers")
    public void parseCustomers(){
        String json= "[{\"userId\":\"123456789\",\"username\":\"elonm\",\"lastname\":\"Musk\",\"firstname\":\"Elon\",\"email\":\"elon.musk@tesla.com\",\"password\":\"$2a$10$69gUK6Z00fBeKRk8j.SIEu/6DbsRcfCHzCH56a86innyCVvrhy0a.\",\"language\":\"English (United States)\",\"birthdate\":\"2022-04-11\"},{\"userId\":\"123456789\",\"username\":\"elonm\",\"lastname\":\"Musk\",\"firstname\":\"Elon\",\"email\":\"elon.musk@tesla.com\",\"password\":\"$2a$10$69gUK6Z00fBeKRk8j.SIEu/6DbsRcfCHzCH56a86innyCVvrhy0a.\",\"language\":\"English (United States)\",\"birthdate\":\"2022-04-11\"},{\"userId\":\"02.05.23-051.44\",\"username\":\"Vion02.05.23-051.44\",\"lastname\":\"Vion\",\"firstname\":\"Francois\",\"email\":\"vion.francois@owo.com\",\"password\":\"$2a$10$y6jN4M5UWdiJ9vA98Rsheu443JnDc2fTzct0zfFqakuv2WR.xME2e\",\"language\":\"French (Belgium)\",\"birthdate\":\"2002-05-23\"},{\"userId\":\"02.05.23-051.44\",\"username\":\"Vion02.05.23-051.44\",\"lastname\":\"Vion\",\"firstname\":\"Francois\",\"email\":\"vion.francois@owo.com\",\"password\":\"$2a$10$y6jN4M5UWdiJ9vA98Rsheu443JnDc2fTzct0zfFqakuv2WR.xME2e\",\"language\":\"French (Belgium)\",\"birthdate\":\"2002-05-23\"},{\"userId\":\"02.05.23-051.44\",\"username\":\"Vion02.05.23-051.44\",\"lastname\":\"Vion\",\"firstname\":\"Francois\",\"email\":\"vion.francois@owo.com\",\"password\":\"$2a$10$y6jN4M5UWdiJ9vA98Rsheu443JnDc2fTzct0zfFqakuv2WR.xME2e\",\"language\":\"French (Belgium)\",\"birthdate\":\"2002-05-23\"},{\"userId\":\"02.05.23-051.44\",\"username\":\"Vion02.05.23-051.44\",\"lastname\":\"Vion\",\"firstname\":\"Francois\",\"email\":\"vion.francois@owo.com\",\"password\":\"$2a$10$y6jN4M5UWdiJ9vA98Rsheu443JnDc2fTzct0zfFqakuv2WR.xME2e\",\"language\":\"French (Belgium)\",\"birthdate\":\"2002-05-23\"},{\"userId\":\"02.05.23-051.44\",\"username\":\"Vion02.05.23-051.44\",\"lastname\":\"Vion\",\"firstname\":\"Francois\",\"email\":\"vion.francois@owo.com\",\"password\":\"$2a$10$y6jN4M5UWdiJ9vA98Rsheu443JnDc2fTzct0zfFqakuv2WR.xME2e\",\"language\":\"French (Belgium)\",\"birthdate\":\"2002-05-23\"},{\"userId\":\"123456789\",\"username\":\"elonm\",\"lastname\":\"Musk\",\"firstname\":\"Elon\",\"email\":\"elon.musk@tesla.com\",\"password\":\"$2a$10$69gUK6Z00fBeKRk8j.SIEu/6DbsRcfCHzCH56a86innyCVvrhy0a.\",\"language\":\"English (United States)\",\"birthdate\":\"2022-04-11\"},{\"userId\":\"123456789\",\"username\":\"elonm\",\"lastname\":\"Musk\",\"firstname\":\"Elon\",\"email\":\"elon.musk@tesla.com\",\"password\":\"$2a$10$69gUK6Z00fBeKRk8j.SIEu/6DbsRcfCHzCH56a86innyCVvrhy0a.\",\"language\":\"English (United States)\",\"birthdate\":\"2022-04-11\"},{\"userId\":\"02.10.31-077.07\",\"username\":\"Moreau02103107707\",\"lastname\":\"Moreau\",\"firstname\":\"Cyril\",\"email\":\"cyril.moreau07@gmail.com\",\"password\":\"$2a$10$s9A4DX2ZHFYxG/1BI..sh.eAcqRvQm1hYKTC2fkL0jBhV5/2HmPGe\",\"language\":\"English (United States)\",\"birthdate\":\"2002-10-31\"},{\"userId\":\"02.10.31-077.07\",\"username\":\"Moreau02103107707\",\"lastname\":\"Moreau\",\"firstname\":\"Cyril\",\"email\":\"cyril.moreau07@gmail.com\",\"password\":\"$2a$10$s9A4DX2ZHFYxG/1BI..sh.eAcqRvQm1hYKTC2fkL0jBhV5/2HmPGe\",\"language\":\"English (United States)\",\"birthdate\":\"2002-10-31\"}]";
        ArrayList<Profile> customersList = Profile.parseCustomer(json);
        assertEquals(customersList.size(), 3);
        assertEquals(customersList.get(1).getNationalRegistrationNumber(), "02.05.23-051.44");
        assertEquals(customersList.get(1).getFirstName(), "Francois");
        assertEquals(customersList.get(1).getLastName(), "Vion");
        assertEquals(customersList.get(0).getNationalRegistrationNumber(), "123456789");
        assertEquals(customersList.get(2).getNationalRegistrationNumber(), "02.10.31-077.07");
    }

    @Test
    @DisplayName("JSON request parser")
    public void parseRequest(){
        String json = "[{\"notificationType\":5,\"comments\":\" BE0000000000006\",\"isFlagged\":false,\"recipientId\":\"ABCDABCD\",\"notificationId\":76,\"date\":\"2022-04-19\",\"senderName\":\"Musk Elon\",\"senderId\":\"123456789\",\"notificationTypeName\":\"TRANSACTION_CANCELED\"},{\"notificationType\":3,\"comments\":\"\",\"isFlagged\":false,\"recipientId\":\"ABCDABCD\",\"notificationId\":50,\"date\":\"2022-04-09\",\"senderName\":\"Musk Elon\",\"senderId\":\"123456789\",\"notificationTypeName\":\"NEW_WALLET\"},{\"notificationType\":0,\"comments\":\"\",\"isFlagged\":false,\"recipientId\":\"ABCDABCD\",\"notificationId\":87,\"date\":\"2022-04-23\",\"senderName\":\"Matos Carlos\",\"senderId\":\"01.02.03-123.00\",\"notificationTypeName\":\"CREATE_ACCOUNT\"}]";
        ArrayList<Request> reqList = Request.parseRequest(json, 0);
        ArrayList<Request> reqList2 = Request.parseRequest(json, 3);
        ArrayList<Request> reqList3 = Request.parseRequest(json, 6);
        assertEquals(reqList.size(), 1);
        assertEquals(reqList2.size(), 1);
        assertEquals(reqList3.size(), 0);
        assertEquals(reqList.get(0).getReason(), CommunicationType.CREATE_ACCOUNT);
        assertEquals(reqList2.get(0).getReason(), CommunicationType.NEW_WALLET);
        assertEquals(reqList.get(0).getSenderID(), "01.02.03-123.00");
        assertEquals(reqList.get(0).getDate(), "2022-04-23");
    }


}
