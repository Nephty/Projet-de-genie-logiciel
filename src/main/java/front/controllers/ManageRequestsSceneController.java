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

public class ManageRequestsSceneController extends Controller implements BackButtonNavigator {

    @FXML
    public Button backButton, transferPermissionRequestsButton, portfolioRequestsButton, requestsStatusButton;

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
    }

    @FXML
    public void handleTransferPermissionRequestsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ManageTransferPermissionRequestsScene));
    }

    @FXML
    public void handlePortfolioRequestsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ManagePortfolioRequestsScene));
    }

    @FXML
    public void handleRequestsStatusButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.RequestsStatusScene));
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
