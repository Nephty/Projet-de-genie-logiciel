package client.front.controllers;

import client.BenkyngApp.Main;
import client.front.navigation.Flow;
import client.front.navigation.navigators.LanguageButtonNavigator;
import client.front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainScreenSceneController extends Controller implements LanguageButtonNavigator {
    @FXML
    public Button changePasswordButton, notificationsButton, requestsButton, financialProductsButton, languageButton, signOutButton;

    @FXML
    public void handleSignOutButtonClicked(MouseEvent event) {
        Main.setScene(Flow.back());
        // TODO : client.back-end : log out (clear any user data)
    }

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
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
