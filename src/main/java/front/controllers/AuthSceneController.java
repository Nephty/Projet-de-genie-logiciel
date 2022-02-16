package front.controllers;

import front.Main;
import front.XML.ResourceBundleBridge;
import front.XML.XMLResolver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthSceneController {
    ResourceBundleBridge colorsResourceBundleBridge = new ResourceBundleBridge(new XMLResolver("values/colors.xml"));

    @FXML
    private Button languageButton, signInButton, signUpButton;

    @FXML
    private Integer pane_background = Integer.parseInt(colorsResourceBundleBridge.get("pane_background"));
    @FXML
    private Integer button_background = Integer.parseInt(colorsResourceBundleBridge.get("button_background"));

    public AuthSceneController() throws IOException {
    }

    @FXML
    private void handleLanguageButtonClicked(MouseEvent event) {
        System.out.println("You clicked the language button");
    }

    @FXML
    private void handleSignInButtonClicked(MouseEvent event) {
        System.out.println("You clicked the sign in button");
        Main.setScene(Main.SignInScene);
    }

    @FXML
    private void handleSignUpButtonClicked(MouseEvent event) {
        System.out.println("You clicked the sign up button");
    }



    //@Override
    public void initialize(URL url, ResourceBundle resources) {
        // Initialization code can go here.
        // The parameters url and resources can be omitted if they are not needed
    }

}