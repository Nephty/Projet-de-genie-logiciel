package front.controllers;

import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class SignUpSceneController implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    Button backButton, languageButton, signUpButton;
    @FXML
    TextField lastNameField, firstNameField, NRNField, emailAddressField, usernameField;
    @FXML
    PasswordField passwordField, confirmPasswordField;
    @FXML
    Label favoriteLanguageLabel;
    @FXML
    ComboBox<String> languageComboBox;
    @FXML
    CheckBox checkBox;

    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    public void handleSignUpButtonClicked(MouseEvent mouseEvent) {
        // TODO : back end signing up process implementation
    }
}
