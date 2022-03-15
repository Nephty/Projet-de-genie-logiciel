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

import java.io.File;

public class ImportDataSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, chooseFileButton, importButton;
    @FXML
    public Label chooseFileLabel, importSuccessfulLabel, noFileSelectedLabel;

    private boolean importDone = false;
    private File selectedFile;  // TODO : is this the right class we should use ?

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        importDone = false;
        selectedFile = null;
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateImportButtonClicked();
        }
    }

    private void emulateImportButtonClicked() {
        handleImportButtonClicked(null);
    }

    @FXML
    public void handleChooseFileButtonClicked(MouseEvent event) {
        // TODO : back-end : 1. Open the file explorer so the user can choose a path
        //                   2. If the user chooses a path, set the text of the importLocationLabel to the selected path
        //                   (eg : Selected path : /home/username/Documents/file.json), set the file object to whatever it is
        //                   and import the data from the file
    }

    @FXML
    public void handleImportButtonClicked(MouseEvent event) {
        // TODO : back-end : 1. If the user selected a file and the noFileSelectedLabel is visible, hide it
        //                   2. If the user did not select a file and the noFileSelectedLabel is not visible, show it
        //                   3. If the user selected a file and the noFileSelectedLabel is not visible, import the data from the file
        //                   4. After the import is done, set exportDone to true and fade in and out exportSuccessfulLabel
    }
}
