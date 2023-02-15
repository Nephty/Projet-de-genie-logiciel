package front.controllers;

import app.Main;
import back.user.ErrorHandler;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ChangePasswordSceneController extends Controller implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    Button backButton;
    @FXML
    PasswordField currentPasswordField, newPasswordField, confirmNewPasswordField;
    @FXML
    Label incorrectCurrentPasswordLabel, passwordDoesNotMatchLabel, passwordChangedLabel;
    @FXML
    Button changePasswordButton;

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
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (passwordChanged) {
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmNewPasswordField.setText("");
        }
        incorrectCurrentPasswordLabel.setVisible(false);
        passwordDoesNotMatchLabel.setVisible(false);
        passwordChangedLabel.setVisible(false);
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            triggerChangePassword();
            keyEvent.consume();
        }
    }

    public void handleChangePasswordButtonClicked(MouseEvent event) {
        // Fetch the old password
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
            HttpResponse<String> rep = null;
            try {
                rep = Unirest.get("https://flns-spring-test.herokuapp.com/api/bank/" + Main.getBank().getSwiftCode())
                        .header("Authorization", "Bearer " + Main.getToken())
                        .asString();
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
            return rep;
        });
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
            // Changes the password
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response2 = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.put("https://flns-spring-test.herokuapp.com/api/bank")
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .body("{\r\n    \"swift\": \"" + obj.getString("swift") + "\",\r\n    \"name\": \"" + obj.getString("name") + "\",\r\n    \"login\": \"" + " " + "\",\r\n    \"password\": \"" + newPassword + "\",\r\n    \"address\": \"" + obj.getString("address") + "\",\r\n    \"country\": \"" + obj.getString("country") + "\",\r\n    \"defaultCurrencyType\": 0\r\n}")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });

            fadeInAndOutNode(3000, passwordChangedLabel);
            passwordChanged = true;

            // Reset the form
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmNewPasswordField.setText("");
        }
    }

    public void triggerChangePassword() {
        handleChangePasswordButtonClicked(null);
    }

    @FXML
    void handleAnyPasswordFieldKeyPressed() {
        passwordChanged = false;
    }
}
