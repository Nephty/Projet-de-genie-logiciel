package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SignInSceneController extends Controller implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    TextField passwordTextField;
    @FXML
    CheckBox showHidePasswordCheckBox;
    @FXML
    Button languageButton, backButton, signInButton;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label incorrectUsernameOrPasswordLabel;

    String username = "", password = "";

    public void initialize() {
        passwordTextField.managedProperty().bind(showHidePasswordCheckBox.selectedProperty());
        passwordField.managedProperty().bind(showHidePasswordCheckBox.selectedProperty().not());
        // Set visible property : if the checkbox is not ticked, show the password field, else, show the password text field
        passwordTextField.visibleProperty().bind(showHidePasswordCheckBox.selectedProperty());
        passwordField.visibleProperty().bind(showHidePasswordCheckBox.selectedProperty().not());
        // Set selection property : if you type something in the password field, you also type it in the password text field
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
        hideAllLabels();
    }

    @FXML
    public void handleSignInButtonClicked(MouseEvent event) {
        signIn();
    }

    /**
     * Checks if every field is properly filled in. Initializes the sign in process.
     */
    public void signIn() {
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
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignInButtonClicked();
    }

    @FXML
    public void handlePasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignInButtonClicked();
    }

    public void emulateSignInButtonClicked() {
        handleSignInButtonClicked(null);
    }

    public void hideAllLabels() {
        incorrectUsernameOrPasswordLabel.setVisible(false);
    }
}
