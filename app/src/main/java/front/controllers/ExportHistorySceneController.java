package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;

public class ExportHistorySceneController extends Controller implements BackButtonNavigator {
    public static ArrayList<Object> exportData;
    private boolean exportDone = false, directoryChosen = false;
    @FXML
    public Button backButton, choosePathButton, JSONExportButton, CSVExportButton;
    @FXML
    public Label choosePathLabel, noPathSelectedLabel, requestNotSentLabel, exportSuccessfulLabel, exportLocationLabel;

    private File selectedDirectory;
    DirectoryChooser directoryChooser = new DirectoryChooser();

    public static void setExportData(ArrayList<Object> arrayList) {
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
    public void handleBackButtonClicked(MouseEvent event) {
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
    public void handleChoosePathButtonClicked(MouseEvent event) {
        selectedDirectory = directoryChooser.showDialog(Main.getStage());
        if (selectedDirectory != null) {
            exportLocationLabel.setText("Selected path : " + selectedDirectory.getPath());
            directoryChosen = true;
            exportDone = false;
        }
    }

    @FXML
    public void handleJSONExportButtonClicked(MouseEvent event) {
        if (!noPathSelectedLabel.isVisible() && selectedDirectory == null) noPathSelectedLabel.setVisible(true);
        else if (noPathSelectedLabel.isVisible() && selectedDirectory != null) noPathSelectedLabel.setVisible(false);

        if (!noPathSelectedLabel.isVisible()) {
            // TODO : export les données de l'arraylist et non pas du compte actuel de Main
            Main.getCurrentAccount().getSubAccountList().get(0).exportHistory(selectedDirectory.getAbsolutePath(), false);
            exportDone = true;

            fadeInAndOutNode(1000, exportSuccessfulLabel);

            // Reset form
            directoryChosen = false;
            exportLocationLabel.setText("Export location not set.");
            selectedDirectory = null;
        }
    }

    @FXML
    public void handleCSVExportButtonClicked(MouseEvent event) {
        if (!noPathSelectedLabel.isVisible() && selectedDirectory == null) noPathSelectedLabel.setVisible(true);
        else if (noPathSelectedLabel.isVisible() && selectedDirectory != null) noPathSelectedLabel.setVisible(false);

        if (!noPathSelectedLabel.isVisible()) {
            // TODO : export les données de l'arraylist et non pas du compte actuel de Main
            Main.getCurrentAccount().getSubAccountList().get(0).exportHistory(selectedDirectory.getAbsolutePath(), true);
            exportDone = true;

            fadeInAndOutNode(1000, exportSuccessfulLabel);

            // Reset form
            directoryChosen = false;
            exportLocationLabel.setText("Export location not set.");
            selectedDirectory = null;
        }

    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
