package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * @author Arnaud MOREAU
 */
public class AuthSceneController extends Controller implements LanguageButtonNavigator {
    @FXML
    Button languageButton, signInButton, signUpButton;

    @FXML
    void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @FXML
    void handleSignInButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.SignInScene));
    }

    @FXML
    void handleSignUpButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.SignUpScene));
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }
}