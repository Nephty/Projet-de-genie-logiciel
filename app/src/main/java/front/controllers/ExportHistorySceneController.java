package front.controllers;

import app.Main;
import back.user.Account;
import back.user.Transaction;
import back.user.Wallet;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Arnaud MOREAU
 */
public class ExportHistorySceneController extends Controller implements BackButtonNavigator {
    public static ArrayList<Transaction> exportData;
    private boolean exportDone = false, directoryChosen = false;
    @FXML
    Button backButton, choosePathButton, JSONExportButton, CSVExportButton;
    @FXML
    Label choosePathLabel, noPathSelectedLabel, requestNotSentLabel, exportSuccessfulLabel, exportLocationLabel;

    private File selectedDirectory;
    final DirectoryChooser directoryChooser = new DirectoryChooser();

    public static void setExportData(ArrayList<Transaction> arrayList) {
        exportData = arrayList;
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (exportDone) {
            exportLocationLabel.setText("Export location not set.");
            if (noPathSelectedLabel.isVisible()) noPathSelectedLabel.setVisible(false);
            selectedDirectory = null;
            exportDone = false;
            directoryChosen = false;
        }
    }

    @FXML
    void handleChoosePathButtonClicked(MouseEvent event) {
        selectedDirectory = directoryChooser.showDialog(Main.getStage());
        if (selectedDirectory != null) {
            exportLocationLabel.setText("Selected path : " + selectedDirectory.getPath());
            directoryChosen = true;
            exportDone = false;
        }
    }

    @FXML
    void handleJSONExportButtonClicked(MouseEvent event) {
        exportProcess(false);
    }

    @FXML
    void handleCSVExportButtonClicked(MouseEvent event) {
        exportProcess(true);
    }

    private void exportProcess(boolean isCSV) {
        if (!noPathSelectedLabel.isVisible() && directoryChosen) {
            exportHistory(selectedDirectory.getAbsolutePath(), isCSV);
            exportDone = true;

            fadeInAndOutNode(1000, exportSuccessfulLabel);

            // Reset form
            directoryChosen = false;
            exportLocationLabel.setText("Export location not set.");
            selectedDirectory = null;
        }
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }

    public void exportHistory(String path, boolean isCsv) {
        // exportData will never be empty if we look at when the user is able to export :
        // 1> the user is export his transaction history (in this case, we ensure that
        // we export at least one transaction)
        // 2> the user is visualizing his accounts (in this case, we don't export if no account is visualized,
        // ensuring that exportData contains at least one transaction)
        try {
            File file = new File(path + "/transactionHistory" + (isCsv ? ".csv" : ".json"));

            boolean fileCreated = file.createNewFile();

            int counter = 0;
            while (!fileCreated) {
                file = new File(path + "/transactionHistory" + counter + (isCsv ? ".csv" : ".json"));
                counter++;
                fileCreated = file.createNewFile();
            }

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
                    boolean isSender = allAccountsIBANS.contains(t.getSenderIBAN());
                    bw.write(convertToCSV(new String[]{t.getSendingDate(), t.getReceiverName(), t.getReceiverIBAN(), (isSender ? "-" + t.getAmount() + "€" : "+" + t.getAmount() + "€")}) + "\n");
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
