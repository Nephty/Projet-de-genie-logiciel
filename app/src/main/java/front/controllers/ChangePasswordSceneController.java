package front.controllers;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ChangePasswordSceneController extends Controller implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    public Button backButton, languageButton;
    @FXML
    public PasswordField currentPasswordField, newPasswordField, confirmNewPasswordField;
    @FXML
    public Label incorrectCurrentPasswordLabel, passwordDoesNotMatchLabel, passwordChangedLabel;
    @FXML
    public Button changePasswordButton;

    private boolean passwordChanged = false;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (passwordChanged) {
            incorrectCurrentPasswordLabel.setVisible(false);
            passwordDoesNotMatchLabel.setVisible(false);
        }
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
        if (passwordChanged) {
            incorrectCurrentPasswordLabel.setVisible(false);
            passwordDoesNotMatchLabel.setVisible(false);
        }
    }

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @FXML
    public void handleComponentKeyPressed(KeyEvent keyEvent) {
        passwordChanged = false;
    }

    @FXML
    public void handleChangePasswordButtonClicked(MouseEvent event) {
        // Fetch the current password in the API
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.get("https://flns-spring-test.herokuapp.com/api/user/" + Main.getUser().getNationalRegistrationNumber() +"?isUsername=false")
                    .header("Authorization", "Bearer " + Main.getToken())
                    .asString();
            Main.errorCheck(response.getStatus());
        } catch (UnirestException e) {
            Main.ErrorManager(408);
        }
        String body = response.getBody();
        JSONObject obj = new JSONObject(body);

        String currentPasswordFromDatabase = obj.getString("password");
        String currentPasswordFromUser = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String newPasswordConfirmation = confirmNewPasswordField.getText();


        // Check if the password hash matches
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        boolean matches = encoder.matches(currentPasswordFromUser, currentPasswordFromDatabase);

        // If the current password stored in the database doesn't match the input "current password"
        if (!matches || currentPasswordFromUser.equals("") && !incorrectCurrentPasswordLabel.isVisible())
            incorrectCurrentPasswordLabel.setVisible(true);
        else if (matches && !currentPasswordFromUser.equals("") && incorrectCurrentPasswordLabel.isVisible())
            incorrectCurrentPasswordLabel.setVisible(false);
        // If the new password doesn't match the confirmation of the new password
        if (!passwordMatchesAndIsNotEmpty(newPassword, newPasswordConfirmation) && !passwordDoesNotMatchLabel.isVisible())
            passwordDoesNotMatchLabel.setVisible(true);
        else if (passwordMatchesAndIsNotEmpty(newPassword, newPasswordConfirmation) && passwordDoesNotMatchLabel.isVisible())
            passwordDoesNotMatchLabel.setVisible(false);

        // If no label is visible, then the inputs are correct
        if (!incorrectCurrentPasswordLabel.isVisible() && !passwordDoesNotMatchLabel.isVisible()) {
            // Change the password in the database
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response2 = null;
            try {
                response2 = Unirest.put("https://flns-spring-test.herokuapp.com/api/user")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + Main.getToken())
                        .body("{\r\n    \"username\": \"" + obj.getString("username") + "\",\r\n    \"userID\": \"" + obj.getString("userID") + "\",\r\n    \"email\": \"" + obj.getString("email") + "\",\r\n    \"password\": \"" + newPassword + "\",\r\n    \"firstname\": \"" + obj.getString("firstname") + "\",\r\n    \"lastname\": \"" + obj.getString("lastname") + "\",\r\n    \"language\": \"" + obj.getString("language") + "\"\r\n}")
                        .asString();
                Main.errorCheck(response2.getStatus());
            } catch (UnirestException e) {
                Main.ErrorManager(408);
            }

            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 3000;
            FadeOutThread sleepAndFadeOutPasswordChangedLabelFadeThread;
            FadeInTransition.playFromStartOn(passwordChangedLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutPasswordChangedLabelFadeThread = new FadeOutThread();
            sleepAndFadeOutPasswordChangedLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, passwordChangedLabel);

            passwordChanged = true;

            // Reset the form
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmNewPasswordField.setText("");
        }
    }

    public void emulateChangePasswordButtonClicked() {
        handleChangePasswordButtonClicked(null);
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
