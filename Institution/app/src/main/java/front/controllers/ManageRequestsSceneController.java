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

public class ManageRequestsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, transferPermissionRequestsButton, portfolioRequestsButton, accountRemovalRequestsButton;

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
    void handleTransferPermissionRequestsButtonClicked(MouseEvent event) {
        Scenes.ManageTransferPermissionRequestsScene = SceneLoader.load("ManageTransferPermissionRequestsScene.fxml", Main.appLocale);
        Main.setScene(Flow.forward(Scenes.ManageTransferPermissionRequestsScene));
    }

    @FXML
    void handlePortfolioRequestsButtonClicked(MouseEvent event) {
        Scenes.ManagePortfolioRequestsScene = SceneLoader.load("ManagePortfolioRequestsScene.fxml", Main.appLocale);
        Main.setScene(Flow.forward(Scenes.ManagePortfolioRequestsScene));
    }

    @FXML
    void handleAccountRemovalRequestsButtonClicked(MouseEvent mouseEvent) {
        Scenes.ManageAccountRemovalRequestsScene = SceneLoader.load("ManageAccountRemovalScene.fxml", Main.appLocale);
        Main.setScene(Flow.forward(Scenes.ManageAccountRemovalRequestsScene));
    }

    @FXML
    void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
