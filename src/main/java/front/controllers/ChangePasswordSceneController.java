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
        // TODO : back-end : retrieve current password from the database
        // TODO : Change variable name
       String currentPasswordHashFromDatabase = "yes", currentPassword = currentPasswordField.getText(),
              currentPasswordHashFromUser = currentPassword,
              newPassword = newPasswordField.getText(), newPasswordConfirmation = confirmNewPasswordField.getText(),
              newPasswordHash = newPassword, newPasswordConfirmationHash = newPasswordConfirmation;


        // If the current password hash stored in the database doesn't match the hash of the input "current password"
      if (!passwordMatchesAndIsNotEmpty(currentPasswordHashFromUser, currentPasswordHashFromDatabase) && !incorrectCurrentPasswordLabel.isVisible())
           incorrectCurrentPasswordLabel.setVisible(true);
     else if (passwordMatchesAndIsNotEmpty(currentPasswordHashFromUser, currentPasswordHashFromDatabase) && incorrectCurrentPasswordLabel.isVisible())
         incorrectCurrentPasswordLabel.setVisible(false);
      // If the hash of the new password doesn't match the hash of the confirmation of the new password
      if (!passwordMatchesAndIsNotEmpty(newPasswordHash, newPasswordConfirmationHash) && !passwordDoesNotMatchLabel.isVisible())
          passwordDoesNotMatchLabel.setVisible(true);
      else if (passwordMatchesAndIsNotEmpty(newPasswordHash, newPasswordConfirmationHash) && passwordDoesNotMatchLabel.isVisible())
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
}
