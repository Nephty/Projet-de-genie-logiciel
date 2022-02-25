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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SignInSceneController extends Controller implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    Button languageButton, backButton, signInButton;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label incorrectUsernameOrPasswordLabel;

    String username = "", password = "";

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
        hideAllLabels();
    }

    @FXML
    public void handleSignInButtonClicked(MouseEvent event) {
        // TODO : back-end : change this condition to check if the user entered correct credentials
        if (usernameField.getText().equals(username) && passwordField.getText().equals(password)) {
            if (incorrectUsernameOrPasswordLabel.isVisible()) incorrectUsernameOrPasswordLabel.setVisible(false);
            passwordField.setText("");
            usernameField.setText("");
            Main.setScene(Flow.forward(Scenes.MainScreenScene));
        } else {
            if (!incorrectUsernameOrPasswordLabel.isVisible()) incorrectUsernameOrPasswordLabel.setVisible(true);
        }
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        hideAllLabels();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    public void handleUsernameFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) triggerSignIn();
    }

    @FXML
    public void handlePasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) triggerSignIn();
    }

    public void triggerSignIn() {
        handleSignInButtonClicked(null);
    }

    public void hideAllLabels() {
        incorrectUsernameOrPasswordLabel.setVisible(false);
    }
}
