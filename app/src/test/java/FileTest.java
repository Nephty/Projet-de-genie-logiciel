import app.Main;
import back.user.Account;
import back.user.SubAccount;
import back.user.Transaction;
import back.user.Wallet;
import front.controllers.ExportHistorySceneController;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class FileTest {
// https://patouche.github.io/2015/01/17/temporary-folder-rule/
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public static void listFiles(String phase) {
        File[] files = FileUtils.getTempDirectory().listFiles((dir, name) -> name.startsWith("junit"));
        System.out.printf("Fichiers trouvés durant la phase %s : %s \n", phase, Arrays.asList(files));
    }

    @Before
    public void setUp() { listFiles("Before"); }

    @After
    public void tearDown() { listFiles("After"); }

    @BeforeClass
    public static void beforeClass() { listFiles("BeforeClass"); }

    @AfterClass
    public static void afterClass() { listFiles("AfterClass"); }

    @Test
    public void testSomething() throws IOException {
        String path = tmpFolder.getRoot().getAbsolutePath();
        System.out.printf("Le dossier temporaire utilisé est situé : %s \n", path);
        boolean isCsv = false;
        String json = "{\"transactionTypeId\":1,\"senderIban\":\"0123456789ABCDEF\",\"recipientIban\":\"BE01010101010101\",\"currencyId\":0,\"transactionAmount\":1.0,\"transactionDate\":\"2022-04-12\",\"comments\":\"Communication\",\"senderName\":\"Musk Elon\",\"recipientName\":\"Musk Elon\",\"currencyName\":\"EUR\",\"transactionId\":19,\"processed\":true},{\"transactionTypeId\":1,\"senderIban\":\"BE12345678910118\",\"recipientIban\":\"BE01010101010101\",\"currencyId\":0,\"transactionAmount\":20.0,\"transactionDate\":\"2022-04-15\",\"comments\":\"Bonjour\",\"senderName\":\"Moreau Benoit\",\"recipientName\":\"Musk Elon\",\"currencyName\":\"EUR\",\"transactionId\":20,\"processed\":true},{\"transactionTypeId\":1,\"senderIban\":\"BE01010101010101\",\"recipientIban\":\"BE01020300000000\",\"currencyId\":0,\"transactionAmount\":10.0,\"transactionDate\":\"2022-04-22\",\"comments\":\"TestTransaction\",\"senderName\":\"Musk Elon\",\"recipientName\":\"Matos Carlos\",\"currencyName\":\"EUR\",\"transactionId\":39,\"processed\":true}";
        ArrayList<Transaction> exportData = SubAccount.createHistory(json);

        assertDoesNotThrow(()->{
            File file = new File(path + "/transactionHistory" + (isCsv ? ".csv" : ".json"));

            boolean fileCreated = file.createNewFile();

            // Manage the case if a file with the same name already exist
            int counter = 0;
            while (!fileCreated) {
                file = new File(path + "/transactionHistory" + counter + (isCsv ? ".csv" : ".json"));
                counter++;
                fileCreated = file.createNewFile();
            }

            // Put all the accounts IBAN in a list
            ArrayList<String> allAccountsIBANS = new ArrayList<>();

            for (Wallet wallet : Main.getPortfolio().getWalletList()) {
                for (Account account : wallet.getAccountList()) {
                    allAccountsIBANS.add(account.getIBAN());
                }
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            if (isCsv) {
                for (Transaction t : exportData) {
                    // Verify if the user if the sender (Verify if it owns the senderIBAN)
                    boolean isSender = allAccountsIBANS.contains(t.getSenderIBAN());
                    bw.write(ExportHistorySceneController.convertToCSV(new String[]{t.getSendingDate(), t.getReceiverName(), t.getReceiverIBAN(), (isSender ? "-" + t.getAmount() + "€" : "+" + t.getAmount() + "€")}) + "\n");
                }
            } else {
                String res;
                JSONObject obj = new JSONObject();
                ArrayList<JSONObject> jsonList = new ArrayList<>();
                for (Transaction t : exportData) {
                    JSONObject obj2 = new JSONObject();
                    obj2.put("sendingDate", t.getSendingDate());
                    obj2.put("senderName", t.getSenderName());
                    obj2.put("senderIBAN", t.getSenderIBAN());
                    obj2.put("receiverName", t.getReceiverName());
                    obj2.put("receiverIBAN", t.getReceiverIBAN());
                    obj2.put("amount", t.getAmount());
                    jsonList.add(obj2);
                }
                obj.put("transactionList", jsonList);
                res = obj.toString();
                bw.write(res);
            }
            bw.flush();
            bw.close();
            fw.flush();
            fw.close();

            // TODO : Lire le fichier et vérifier les Strings
        });


        // Vous pouvez facilement créer des fichiers ...
//        File file = tmpFolder.newFile("test-file.txt");
//        System.out.printf("Le fichier créé est situé : %s \n", file.getAbsolutePath());

        // Manipuler ces fichiers ...
//        try (OutputStream os = new FileOutputStream(file)) {
//            os.write("toto".getBytes());
//        }

        // Afin de pouvoir les utiliser dans votre test
//        try (InputStream is = new FileInputStream(file)) {
//            byte[] buffer = new byte[4];
//            is.read(buffer);
//            System.out.printf("Contenu du fichier test-file.txt : %s \n", new String(buffer));
//        }

        // Et même créer des dossiers !
//        File folder = tmpFolder.newFolder("test-folder");
//        System.out.printf("Le dossier créé est situé : %s \n", folder.getAbsolutePath());
    }
}