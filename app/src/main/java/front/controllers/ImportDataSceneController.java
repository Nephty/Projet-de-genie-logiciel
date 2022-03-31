package front.controllers;

import app.Main;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import java.io.File;

public class ImportDataSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, choosePathButton, importButton;
    @FXML
    public Label chooseFileLabel, importSuccessfulLabel, noPathSelectedLabel, importFileLabel;

    private boolean importDone = false;
    private File selectedFile;  // TODO : is this the right class we should use ?
    private boolean fileChosen = false;
    private FileChooser exportDataFileChooser;
    
    @Override
    public void initialize() {
        exportDataFileChooser = new FileChooser();
        ExtensionFilter JSONFilter = new ExtensionFilter("JSON files (*.json)", "*.json");
        ExtensionFilter CSVFilter = new ExtensionFilter("CSV files (*.csv)", "*.csv");
        exportDataFileChooser.getExtensionFilters().add(JSONFilter);
        exportDataFileChooser.getExtensionFilters().add(CSVFilter);
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
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (importDone) {
            selectedFile = null;
            importFileLabel.setText("Import file not set.");
            importDone = false;
            fileChosen = false;
        }
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateImportButtonClicked();
            keyEvent.consume();
        }
    }

    private void emulateImportButtonClicked() {
        handleImportButtonClicked(null);
    }

    @FXML
    public void handleChooseFileButtonClicked(MouseEvent event) {
        selectedFile = exportDataFileChooser.showOpenDialog(Main.getStage());
        if (selectedFile != null) {
            // File chosen (in file variable)
            // We don't need to test if it equals null : when we arrive on the scene and if the user doesn't select any
            // file, the label is already saying "no file selected", and when the user selects a file, then re-opens
            // the file chooser and closes it (returning null), we want to keep the previously selected file
            importFileLabel.setText("File chosen : " + selectedFile.getName());
            fileChosen = true;
            // If we imported a file and choose another file, we reset import done so that going back won't reset the form
            importDone = false;
        }
    }

    @FXML
    public void handleImportButtonClicked(MouseEvent event) {
        if (!noPathSelectedLabel.isVisible() && !fileChosen) noPathSelectedLabel.setVisible(true);
        else if (noPathSelectedLabel.isVisible() && fileChosen) noPathSelectedLabel.setVisible(false);

        if (!noPathSelectedLabel.isVisible()) {
            // If the user selected a file

            // TODO : back-end : import data from the selected file

            fadeInAndOutNode(1000, importSuccessfulLabel);
            importDone = true;

            // Reset form
            fileChosen = false;
            importFileLabel.setText("Import file not set.");
            selectedFile = null;
        }
    }
}
