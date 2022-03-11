package front.controllers;

import BenkyngApp.Main;
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
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmNewPasswordField.setText("");
        }
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    public void handleCurrentPasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) triggerChangePassword();
    }

    public void handleNewPasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) triggerChangePassword();
    }

    public void handleConfirmNewPasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) triggerChangePassword();
    }

    public void handleChangePasswordButtonClicked(MouseEvent event) {
        // TODO : back-end : Chercher le mot de passe actuel sur l'api
        String currentPasswordFromDatabase = "yes";
        String currentPasswordFromUser = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String newPasswordConfirmation = confirmNewPasswordField.getText();


        // If the current password stored in the database doesn't match the input "current password"
      if (!passwordMatchesAndIsNotEmpty(currentPasswordFromUser, currentPasswordFromDatabase) && !incorrectCurrentPasswordLabel.isVisible())
           incorrectCurrentPasswordLabel.setVisible(true);
     else if (passwordMatchesAndIsNotEmpty(currentPasswordFromUser, currentPasswordFromDatabase) && incorrectCurrentPasswordLabel.isVisible())
         incorrectCurrentPasswordLabel.setVisible(false);
      // If the new password doesn't match the confirmation of the new password
      if (!passwordMatchesAndIsNotEmpty(newPassword, newPasswordConfirmation) && !passwordDoesNotMatchLabel.isVisible())
          passwordDoesNotMatchLabel.setVisible(true);
      else if (passwordMatchesAndIsNotEmpty(newPassword, newPasswordConfirmation) && passwordDoesNotMatchLabel.isVisible())
          passwordDoesNotMatchLabel.setVisible(false);

        // If no label is visible, then the inputs are correct
        if (!incorrectCurrentPasswordLabel.isVisible() && !passwordDoesNotMatchLabel.isVisible()) {
            // TODO : back-end : change the password in the database
            
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 3000;
            FadeOutThread sleepAndFadeOutPasswordChangedLabelFadeThread;
            FadeInTransition.playFromStartOn(passwordChangedLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutPasswordChangedLabelFadeThread = new FadeOutThread();
            sleepAndFadeOutPasswordChangedLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, passwordChangedLabel);
            
            passwordChanged = true;
            
        }
    }

    public void triggerChangePassword() {
        handleChangePasswordButtonClicked(null);
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
