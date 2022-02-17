package front.controllers;

import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class SignInSceneController implements BackButtonNavigator, LanguageButtonNavigator {

    @FXML
    public Button languageButton, backButton, signInButton;

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @FXML
    public void handleSignInButtonClicked(MouseEvent event) {
        // TODO : sign in button navigation
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Main.LanguageScene));
    }
}
