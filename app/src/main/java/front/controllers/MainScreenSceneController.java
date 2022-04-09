package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import static app.Main.appLocale;

public class MainScreenSceneController extends Controller {
    @FXML
    public Button changePasswordButton, notificationsButton, requestsButton, visualizeToolButton, financialProductsButton, signOutButton;

    @FXML
    public void handleSignOutButtonClicked(MouseEvent event) {
        Main.clearData();
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleFinancialProductsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.FinancialProductsScene));
    }

    @FXML
    public void handleRequestsButtonClicked(MouseEvent event) {
        Scenes.RequestsScene = SceneLoader.load("RequestsScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.RequestsScene));
    }

    @FXML
    public void handleNotificationsButtonClicked(MouseEvent event) {
        Scenes.NotificationsScene = SceneLoader.load("NotificationsScene.fxml", appLocale);
        Main.setScene(Flow.forward(Scenes.NotificationsScene));
    }

    @FXML
    public void handleChangePasswordButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ChangePasswordScene));
    }

    @FXML
    public void handleVisualizeToolButtonClicked(MouseEvent mouseEvent) {
        Main.setScene(Flow.forward(Scenes.VisualizeToolScene));
    }
}
