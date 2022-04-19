package front.controllers;

import app.Main;
import back.user.Profile;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import static app.Main.appLocale;

/**
 * @author Arnaud MOREAU
 */
public class SignInSceneController extends Controller implements BackButtonNavigator {
    @FXML
    TextField passwordTextField;
    @FXML
    CheckBox showHidePasswordCheckBox;
    @FXML
    Button backButton, signInButton;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label incorrectUsernameOrPasswordLabel;

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
    void handleSignInButtonClicked(MouseEvent event) {
        signIn();
    }

    /**
     * Checks if every field is properly filled in. Initializes the sign-in process.
     */
    public void signIn() {
        // Login with username and password
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("https://flns-spring-test.herokuapp.com/api/login")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("username", usernameField.getText())
                    .field("password", passwordField.getText())
                    .field("role", "ROLE_USER")
                    .asString();
            if(response.getStatus() != 403){
                Main.errorCheck(response.getStatus());
            }
        } catch (UnirestException e) {
            Main.ErrorManager(408);
        }

        // If the response is correct, initialise the tokens
        if (response != null) {
            if (response.getStatus() == 200) {
                if (incorrectUsernameOrPasswordLabel.isVisible()) incorrectUsernameOrPasswordLabel.setVisible(false);
                String body = response.getBody();
                JSONObject obj = new JSONObject(body);
                Main.setToken(obj.getString("access_token"));
                Main.setRefreshToken(obj.getString("refresh_token"));
                // Creates the user
                try {
                    Unirest.setTimeouts(0, 0);
                    HttpResponse<String> response2 = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + usernameField.getText() + "?isUsername=true")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .asString();
                    Main.errorCheck(response2.getStatus());
                    String body2 = response2.getBody();
                    JSONObject obj2 = new JSONObject(body2);
                    Main.setUser(new Profile(obj2.getString("firstname"), obj2.getString("lastname"), obj2.getString("username"), obj2.getString("language"), obj2.getString("userId")));
                } catch (UnirestException e) {
                    Main.ErrorManager(408);
                }
                String favoriteLanguage = Main.getUser().getFavoriteLanguage();
                for (Locale locale : Arrays.asList(Main.EN_US_Locale, Main.FR_BE_Locale, Main.NL_NL_Locale, Main.PT_PT_Locale, Main.LT_LT_Locale, Main.RU_RU_Locale, Main.DE_DE_Locale, Main.PL_PL_Locale)) {
                    if (locale.getDisplayName().equals(favoriteLanguage)) {
                        appLocale = locale;
                        break;
                    }
                }
                // Creates user's portfolio
                Main.updatePortfolio();
                // Load most of the scenes
                Scenes.MainScreenScene = SceneLoader.load("MainScreenScene.fxml", appLocale);
                Scenes.ChangePasswordScene = SceneLoader.load("ChangePasswordScene.fxml", appLocale);
                Scenes.FinancialProductsScene = SceneLoader.load("FinancialProductsScene.fxml", appLocale);
                Scenes.ExportHistoryScene = SceneLoader.load("ExportHistoryScene.fxml", appLocale);
                Scenes.TransferScene = SceneLoader.load("TransferScene.fxml", appLocale);
                Scenes.EnterPINScene = SceneLoader.load("EnterPINScene.fxml", appLocale);
                Scenes.VisualizeToolScene = SceneLoader.load("VisualizeToolScene.fxml", appLocale);
                Main.setScene(Flow.forward(Scenes.MainScreenScene));
                // Empty username text field and password field
                usernameField.setText("");
                passwordField.setText("");
            } else {
                if (!incorrectUsernameOrPasswordLabel.isVisible()) incorrectUsernameOrPasswordLabel.setVisible(true);
            }
        }
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
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

    @FXML
    void handleComponentKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignInButtonClicked();
        keyEvent.consume();
    }

    public void emulateSignInButtonClicked() {
        handleSignInButtonClicked(null);
    }

    public void hideAllLabels() {
        incorrectUsernameOrPasswordLabel.setVisible(false);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        } else if (event.getCode() == KeyCode.ENTER) {
            emulateSignInButtonClicked();
            event.consume();
        }
    }
}
