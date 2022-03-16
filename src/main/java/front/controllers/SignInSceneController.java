package front.controllers;

import app.Main;
import back.user.Profile;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;

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
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", usernameField.getText())
                    .field("password", passwordField.getText())
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }


        if (response.getStatus() == 200) {
            if (incorrectUsernameOrPasswordLabel.isVisible()) incorrectUsernameOrPasswordLabel.setVisible(false);
            String body = response.getBody();
            JSONObject obj = new JSONObject(body);
            Main.setToken(obj.getString("access_token"));
            Main.setRefreshToken(obj.getString("refresh_token"));
            try {
                Main.setUser(new Profile("02.05.23-051.44")); // TODO : Changer ça et faire une requête pour avoir l'id avec l'username
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            Main.updatePortfolio();
            Scenes.NotificationsScene = SceneLoader.load("NotificationsScene.fxml");
            Scenes.RequestsScene = SceneLoader.load("RequestsScene.fxml");
            Scenes.RequestsStatusScene = SceneLoader.load("RequestsStatusScene.fxml");
            Scenes.RequestNewPortfolioScene = SceneLoader.load("RequestNewPortfolioScene.fxml");
            Scenes.RequestTransferPermissionScene = SceneLoader.load("RequestTransferPermissionScene.fxml");
            Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml");
            Scenes.FinancialProductsScene = SceneLoader.load("FinancialProductsScene.fxml");
            Scenes.TransactionsHistoryScene = SceneLoader.load("TransactionsHistoryScene.fxml");
            Scenes.ExportHistoryScene = SceneLoader.load("ExportHistoryScene.fxml");
            Scenes.TransferScene = SceneLoader.load("TransferScene.fxml");
            Scenes.EnterPINScene = SceneLoader.load("EnterPINScene.fxml");
            Scenes.VisualizeToolScene = SceneLoader.load("VisualizeToolScene.fxml");
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
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    public void handleComponentKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignInButtonClicked();
    }

    public void emulateSignInButtonClicked() {
        handleSignInButtonClicked(null);
    }

    public void hideAllLabels() {
        incorrectUsernameOrPasswordLabel.setVisible(false);
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        } else if (event.getCode() == KeyCode.ENTER) {
            emulateSignInButtonClicked();
            event.consume();
        }
    }
}
