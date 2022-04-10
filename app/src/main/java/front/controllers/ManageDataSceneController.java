package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ManageDataSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, importDataButton, exportAllClientDataButton;

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
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateBackButtonClicked();
            keyEvent.consume();
        }
    }

    @FXML
    void handleImportDataButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ImportDataScene));
    }

    @FXML
    void handleExportAllClientDataButtonClicked(MouseEvent event) {
        // TODO : back-end : set export data to all clients data
        // ExportDataSceneController.setExportData(ALL CLIENTS);
        Main.setScene(Flow.forward(Scenes.ExportDataScene));
    }
}
