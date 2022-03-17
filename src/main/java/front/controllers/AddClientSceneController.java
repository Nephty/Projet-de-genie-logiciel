package front.controllers;

import app.Main;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class AddClientSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, addClientButton;
    @FXML
    public Label enterNRNLabel, invalidNRNdLabel, requestNotSentLabel, clientAddedLabel;
    @FXML
    public TextField NRNTextField;

    private boolean clientAdded = false;

    /**
     * Checks if the given string is a valid NRN.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must match the format XX.XX.XX-XXX.XX where X in an integer in range 0-9
     *
     * @param NRN - <code>String</code> - the NRN to check
     * @return <code>boolean</code> whether the given NRN is a valid NRN or not
     */
    public static boolean isValidNRN(String NRN) {
        if (NRN == null) return false;
        if (NRN.length() != 15) return false;  // NRN.length() == 15 already checks NRN != ""
        for (int i = 0; i < 15; i++) {
            switch (i) {
                case 0:
                case 1:
                case 3:
                case 4:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                    if (!Character.isDigit(NRN.charAt(i))) return false;
                    break;
                case 2:
                case 5:
                case 12:
                    if (NRN.charAt(i) != '.') return false;
                    break;
                case 8:
                    if (NRN.charAt(i) != '-') return false;
                    break;
            }
        }
        return true;
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateAddClientButtonClicked();
            keyEvent.consume();
        }
    }

    @FXML
    public void handleAddClientButtonClicked(MouseEvent event) {
        String NRN = NRNTextField.getText();

        if (!isValidNRN(NRN) && !invalidNRNdLabel.isVisible()) invalidNRNdLabel.setVisible(true);
        else if (isValidNRN(NRN) && invalidNRNdLabel.isVisible()) invalidNRNdLabel.setVisible(false);

        if (!invalidNRNdLabel.isVisible()) { // TODO : check if the client is not already in the database
            // TODO : back-end : add client
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 3000;
            FadeOutThread sleepAndFadeOutRequestSentLabelFadeThread;
            FadeInTransition.playFromStartOn(clientAddedLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutRequestSentLabelFadeThread = new FadeOutThread();
            sleepAndFadeOutRequestSentLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, clientAddedLabel);
            clientAdded = true;

            // Reset the form
            NRNTextField.setText("");
        }
    }

    private void emulateAddClientButtonClicked() {
        handleAddClientButtonClicked(null);
    }

    @FXML
    public void handleNRNTextFieldKeyPressed(KeyEvent keyEvent) {
        clientAdded = false;
    }
}
