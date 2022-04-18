package front.controllers;

import app.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * @author Arnaud MOREAU
 */
public class EnterPINSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, confirmButton;
    @FXML
    PasswordField PINField;
    @FXML
    Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, buttonDel;
    @FXML
    Label incorrectPINLabel, correctPINLabel, tooManyAttemptsLabel;
    private int attempts = 0;
    private boolean executingTransfer = false;

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
        attempts = 0;
        tooManyAttemptsLabel.setVisible(false);
    }

    @FXML
    void handleConfirmButtonClicked(MouseEvent event) {
        if (!executingTransfer) {
            executingTransfer = true;
            attempts++;
            if (attempts <= 3) {
                boolean PINCorrect = checkPINIsCorrect(PINField.getText());
                incorrectPINLabel.setVisible(!PINCorrect);
                if (PINCorrect) {
                    fadeInAndOutNode(3000, correctPINLabel);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                    emulateBackButtonMouseClicked();
                    tooManyAttemptsLabel.setVisible(false);
                    PINField.setText("");
                }
            } else {
                tooManyAttemptsLabel.setVisible(true);
            }
        }
        executingTransfer = false;
    }

    private boolean checkPINIsCorrect(String PIN) {
        return PIN.equals("0000");
    }

    @FXML
    void handleButtonKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
                emulateConfirmButtonMouseClicked();
                break;
            case DELETE:
            case BACK_SPACE:
                emulateDeletedButtonMouseClicked();
                break;
            case NUMPAD0:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
            case NUMPAD6:
            case NUMPAD7:
            case NUMPAD8:
            case NUMPAD9:
                emulateNumButtonMouseClicked(Integer.parseInt(keyEvent.getCode().toString().substring(keyEvent.getCode().toString().length() - 1)));
                break;
        }
    }

    @FXML
    void handleNumButtonMouseClicked(MouseEvent event) {
        Button buttonSource = (Button) event.getSource();
        if (buttonSource.getText().equals("<")) {
            if (PINField.getText().length() > 0) {
                int length = PINField.getText().length();
                PINField.setText(PINField.getText().substring(0, length - 1));
            }
        } else {
            if (PINField.getText().length() < 4) {
                PINField.setText(PINField.getText() + buttonSource.getText());
            }
        }
    }

    private void emulateConfirmButtonMouseClicked() {
        handleConfirmButtonClicked(null);
    }

    private void emulateDeletedButtonMouseClicked() {
        if (PINField.getText().length() > 0) {
            int length = PINField.getText().length();
            PINField.setText(PINField.getText().substring(0, length - 1));
        }
    }

    private void emulateNumButtonMouseClicked(int num) {
        if (PINField.getText().length() < 4) {
            PINField.setText(PINField.getText() + num);
        }
    }

    private void emulateBackButtonMouseClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
