package front.controllers;

import app.Main;
import back.user.Transaction;
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
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportHistorySceneController extends Controller implements BackButtonNavigator {
    public static ArrayList<Transaction> exportData;
    private boolean exportDone = false, directoryChosen = false;
    @FXML
    Button backButton, choosePathButton, JSONExportButton, CSVExportButton;
    @FXML
    Label choosePathLabel, noPathSelectedLabel, requestNotSentLabel, exportSuccessfulLabel, exportLocationLabel;

    private File selectedDirectory;
    DirectoryChooser directoryChooser = new DirectoryChooser();

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

    // TODO : don't forget form reset

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
        // TODO : Gérer les cas où le fichier existe, etc
        // TODO : Afficher un truc quand c'est fait
        // TODO : Historique de transaction vide ?
        try {
            File file = new File(path + "/transactionHistory" + (isCsv ? ".csv" : ".json"));

            boolean fileCreated = file.createNewFile();

            if (fileCreated) {
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                if (isCsv) {
                    for (Transaction t : exportData) {
                        // TODO : how to know if the user is the sender for every transaction in the data to export ?
                        boolean isSender = t.getSenderIBAN().equals(Main.getCurrentAccount().getIBAN());
                        bw.write(convertToCSV(new String[]{t.getSendingDate(), t.getReceiverName(), t.getReceiverIBAN(), ( isSender ? "-" : "+" + t.getAmount() + "€")}) + "\n");
                    }
                } else {
                    String res = "";
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
            } else throw new FileSystemException("File could not be created.");

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
