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

import java.util.ArrayList;

public class ExportDataSceneController extends Controller implements BackButtonNavigator {
    private static ArrayList<Profile> exportData;
    private final boolean exportDone = false;
    @FXML
    Button backButton, choosePathButton, JSONExportButton, CSVExportButton;
    @FXML
    Label choosePathLabel, noPathSelectedLabel, requestNotSentLabel, exportSuccessfulLabel, exportLocationLabel;

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
        // TODO : back-end : 1. Open the file explorer so the user can choose a path
        //                   2. If the user chooses a path, set the text of the exportLocationLabel to the selected path
        //                   (eg : Selected path : /home/username/Documents), set the file object to whatever it is
        //                   and export the data to the file
    }

    @FXML
    void handleJSONExportButtonClicked(MouseEvent event) {
        // TODO : back-end : 1. If the user selected a path and the noPathSelectedLabel is visible, hide it
        //                   2. If the user did not select a path and the noPathSelectedLabel is not visible, show it
        //                   3. If the user selected a path and the noPathSelectedLabel is not visible, export to JSON at the selected path
        //                   4. After the export is done, set exportDone to true and fade in and out exportSuccessfulLabel
//        Profile.exportClientData(exportData, path,false);
    }

    @FXML
    void handleCSVExportButtonClicked(MouseEvent event) {
        // TODO : back-end : 1. If the user selected a path and the noPathSelectedLabel is visible, hide it
        //                   2. If the user did not select a path and the noPathSelectedLabel is not visible, show it
        //                   3. If the user selected a path and the noPathSelectedLabel is not visible, export to CSV at the selected path
        //                   4. After the export is done, set exportDone to true and set exportSuccessfulLabel visibility to true
//        Profile.exportClientData(exportData, path,true);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
