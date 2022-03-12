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
    public Button changePasswordButton, manageDataButton, ManageRequestsButton, ClientsButton, languageButton, signOutButton;

    @FXML
    public void handleSignOutButtonClicked(MouseEvent event) {
        Main.setScene(Flow.back());
        // TODO : back-end : log out (clear any user data)
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
    public void handleClientsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ClientsScene));
    }

    @FXML
    public void handleManageRequestsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ManageRequestsScene));
    }

    @FXML
    public void handleManageDataButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ManageDataScene));
    }

    @FXML
    public void handleChangePasswordButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ChangePasswordScene));
    }
}
