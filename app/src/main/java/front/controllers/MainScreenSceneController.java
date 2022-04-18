package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import static app.Main.appLocale;

/**
 * @author Arnaud MOREAU
 */
public class MainScreenSceneController extends Controller {
    @FXML
    Button changePasswordButton, notificationsButton, requestsButton, visualizeToolButton, financialProductsButton, signOutButton;

    @FXML
    void handleSignOutButtonClicked(MouseEvent event) {
        Main.clearData();
        Main.setScene(Flow.back());
    }

    @FXML
    void handleFinancialProductsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.FinancialProductsScene));
    }

    @FXML
    void handleRequestsButtonClicked(MouseEvent event) {
        Scenes.RequestsScene = SceneLoader.load("RequestsScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.RequestsScene));
    }

    @FXML
    void handleNotificationsButtonClicked(MouseEvent event) {
        Scenes.NotificationsScene = SceneLoader.load("NotificationsScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.NotificationsScene));
    }

    @FXML
    void handleChangePasswordButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ChangePasswordScene));
    }

    @FXML
    void handleVisualizeToolButtonClicked(MouseEvent mouseEvent) {
        Main.setScene(Flow.forward(Scenes.VisualizeToolScene));
    }
}
