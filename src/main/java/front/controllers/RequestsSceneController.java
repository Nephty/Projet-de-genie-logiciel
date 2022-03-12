package front.controllers;

import App.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class RequestsSceneController extends Controller implements BackButtonNavigator {

    @FXML
    public Button backButton, transferPermissionButton, newPortfolioButton, requestsStatusButton;

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
    public void handleTransferPermissionButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.RequestTransferPermissionScene));
    }

    @FXML
    public void handleNewPortfolioButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.RequestNewPortfolioScene));
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
