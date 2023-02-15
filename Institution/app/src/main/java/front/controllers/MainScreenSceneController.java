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
    Button changePasswordButton, manageDataButton, manageRequestsButton, clientsButton, signOutButton;

    @FXML
    void handleSignOutButtonClicked(MouseEvent event) {
        Main.setScene(Flow.back());
        Main.clearData();
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    void handleClientsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ClientsScene));
    }

    @FXML
    void handleManageRequestsButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ManageRequestsScene));
    }

    @FXML
    void handleManageDataButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ManageDataScene));
    }

    @FXML
    void handleChangePasswordButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ChangePasswordScene));
    }
}
