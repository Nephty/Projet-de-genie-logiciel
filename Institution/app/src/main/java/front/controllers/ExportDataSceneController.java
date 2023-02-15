package front.controllers;

import app.Main;
import back.user.Profile;
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

public class ExportDataSceneController extends Controller implements BackButtonNavigator {
    private static ArrayList<Profile> exportData;
    private boolean exportDone = false;
    @FXML
    Button backButton, choosePathButton, JSONExportButton, CSVExportButton;
    @FXML
    Label choosePathLabel, noPathSelectedLabel, requestNotSentLabel, exportSuccessfulLabel, exportLocationLabel;

    private boolean directoryChosen = false;
    private File selectedDirectory;
    private DirectoryChooser directoryChooser = new DirectoryChooser();

    public static void setExportData(ArrayList<Profile> arrayList) {
        exportData = arrayList;
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        exportLocationLabel.setText("Export location not set.");
        if (exportDone) {
            if (noPathSelectedLabel.isVisible()) noPathSelectedLabel.setVisible(false);
            // PRO TIP : no need to set exportData to null when exiting the scene, because to come back to the scene
            // the user has to select items from the clients view list and click on the export data button,
            // which is automatically going to erase the actual data and replace it with newer data
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
        if (!noPathSelectedLabel.isVisible() && selectedDirectory == null) noPathSelectedLabel.setVisible(true);
        else if (noPathSelectedLabel.isVisible() && selectedDirectory != null) noPathSelectedLabel.setVisible(false);

        if (!noPathSelectedLabel.isVisible() && directoryChosen) {
            Profile.exportClientData(exportData, selectedDirectory.getAbsolutePath(),isCSV);

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
}
