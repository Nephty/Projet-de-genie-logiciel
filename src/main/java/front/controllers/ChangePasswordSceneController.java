package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ChangePasswordSceneController extends Controller implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    public Button backButton, languageButton;
    @FXML
    public PasswordField currentPasswordField, newPasswordField, confirmNewPasswordField;
    @FXML
    public Label incorrectCurrentPassword, passwordDoesNotMatch;
    @FXML
    public Button changePasswordButton;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    public void handleCurrentPasswordFieldKeyPressed(KeyEvent keyEvent) {
    }

    public void handleNewPasswordFieldKeyPressed(KeyEvent keyEvent) {
    }

    public void handleConfirmNewPasswordFieldKeyPressed(KeyEvent keyEvent) {
    }

    public void handleChangePasswordButtonClicked(MouseEvent event) {
    }
}
