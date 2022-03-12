package front.controllers;

import App.Main;
import front.navigation.Flow;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class AuthSceneController extends Controller implements LanguageButtonNavigator {
    @FXML
    Button languageButton, signInButton, signUpButton;

    public AuthSceneController() {
    }

    @FXML
    private void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @FXML
    private void handleSignInButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.SignInScene));
    }

    @FXML
    private void handleSignUpButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.SignUpScene));
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }
}