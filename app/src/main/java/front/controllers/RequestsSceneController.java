package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import static app.Main.appLocale;

/**
 * @author Arnaud MOREAU
 */
public class RequestsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, transferPermissionButton, newPortfolioButton, requestsStatusButton;

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
    }

    @FXML
    void handleTransferPermissionButtonClicked(MouseEvent event) {
        Scenes.RequestTransferPermissionScene = SceneLoader.load("RequestTransferPermissionScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.RequestTransferPermissionScene));
    }

    @FXML
    void handleNewPortfolioButtonClicked(MouseEvent event) {
        Scenes.RequestNewPortfolioScene = SceneLoader.load("RequestNewPortfolioScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.RequestNewPortfolioScene));
    }

    @FXML
    void handleRequestsStatusButtonClicked(MouseEvent event) {
        Scenes.RequestsStatusScene = SceneLoader.load("RequestsStatusScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.RequestsStatusScene));
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
