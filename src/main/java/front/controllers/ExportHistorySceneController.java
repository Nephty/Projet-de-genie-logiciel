package front.controllers;

import App.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ExportHistorySceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, choosePathButton, JSONExportButton, CSVExportButton;
    @FXML
    public Label choosePathLabel, noPathSelectedLabel, requestNotSentLabel, exportSuccessfulLabel, exportLocationLabel;

    private boolean exportDone = false;

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
        exportLocationLabel.setText("Export location not set.");
        if (exportDone) {
            if (noPathSelectedLabel.isVisible()) noPathSelectedLabel.setVisible(false);
        }
    }

    @FXML
    public void handleChoosePathButtonClicked(MouseEvent event) {
        // TODO : back-end ? : 1. Open the file explorer so the user can choose a path
        //                     2. If the user chooses a path, set the text of the exportLocationLabel to the selected path (eg : Selected path : /home/username/Documents)
    }

    @FXML
    public void handleJSONExportButtonClicked(MouseEvent event) {
        // TODO : back-end : 1. If the user selected a path and the noPathSelectedLabel is visible, hide it
        //                   2. If the user did not select a path and the noPathSelectedLabel is not visible, show it
        //                   3. If the user selected a path and the noPathSelectedLabel is no visible, export to JSON at the selected path
        //                   4. After the export is done, set exportDone to true and fade in and out exportSuccessfulLabel
    }

    @FXML
    public void handleCSVExportButtonClicked(MouseEvent event) {
        // TODO : back-end : 1. If the user selected a path and the noPathSelectedLabel is visible, hide it
        //                   2. If the user did not select a path and the noPathSelectedLabel is not visible, show it
        //                   3. If the user selected a path and the noPathSelectedLabel is no visible, export to CSV at the selected path
        //                   4. After the export is done, set exportDone to true and set exportSuccessfulLabel visibility to true
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
