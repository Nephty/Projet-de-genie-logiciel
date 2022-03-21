package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainScreenSceneController extends Controller implements LanguageButtonNavigator {
    @FXML
    public Button changePasswordButton, notificationsButton, requestsButton, financialProductsButton, signOutButton;

    @FXML
    public void handleSignOutButtonClicked(MouseEvent event) {
        Main.clearData();
        Main.setScene(Flow.back());
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    public void handleFinancialProductsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.FinancialProductsScene));
    }

    @FXML
    public void handleRequestsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.RequestsScene));
    }

    @FXML
    public void handleNotificationsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.NotificationsScene));
    }

    @FXML
    public void handleChangePasswordButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ChangePasswordScene));
    }
}
