package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainScreenSceneController extends Controller implements LanguageButtonNavigator {
    @FXML
    public Button changePasswordButton, notificationsButton, requestsButton, financialProductsButton, languageButton, signOutButton;

    public void handleSignOutButtonClicked(MouseEvent event) {
        Main.setScene(Flow.back());
        // TODO : back-end log out (clear any user data)
    }

    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    public void handleFinancialProductsButtonClicked(MouseEvent event) {
    }

    public void handleRequestsButtonClicked(MouseEvent event) {
    }

    public void handleNotificationsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.NotificationsScene));
    }

    public void handleChangePasswordButtonClicked(MouseEvent event) {
    }
}
