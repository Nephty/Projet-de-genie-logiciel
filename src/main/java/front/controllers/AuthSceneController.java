package front.controllers;

import front.Main;
import front.XML.ResourceBundleBridge;
import front.XML.XMLResolver;
import front.navigation.Flow;
import front.navigation.navigators.LanguageButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthSceneController implements LanguageButtonNavigator {
    ResourceBundleBridge colorsResourceBundleBridge = new ResourceBundleBridge(new XMLResolver("values/colors.xml"));

    @FXML
    private Button languageButton, signInButton, signUpButton;

    public AuthSceneController() throws IOException {
    }

    @FXML
    private void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @FXML
    private void handleSignInButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Main.SignInScene));
    }

    @FXML
    private void handleSignUpButtonClicked(MouseEvent event) {
        // TODO : sign up button navigation
        System.out.println("You clicked the sign up button");
    }



    //@Override
    public void initialize(URL url, ResourceBundle resources) {
        // Initialization code can go here.
        // The parameters url and resources can be omitted if they are not needed
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        // TODO : language window navigation
    }
}