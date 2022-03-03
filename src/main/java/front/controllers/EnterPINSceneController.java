package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class EnterPINSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, confirmButton;
    @FXML
    public PasswordField PINField;
    @FXML
    public Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, buttonDel;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleConfirmButtonClicked(MouseEvent event) {
        // TODO : back to the transfer scene, bring the positive or negative response
    }

    @FXML
    public void handleButtonKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateConfirmButtonMouseClicked();
        }
    }

    @FXML
    public void handleNumButtonMouseClicked(MouseEvent event) {
        Button buttonSource = (Button) event.getSource();
        if (buttonSource.getText().equals("<")) {
            if (PINField.getText().length() > 0) {
                PINField.setText(PINField.getText().substring(0, PINField.getText().length()-1));
            }
        } else {
            if (PINField.getText().length() < 4) {
                PINField.setText(PINField.getText() + buttonSource.getText());
            }
        }
    }

    public void emulateConfirmButtonMouseClicked() {
        handleConfirmButtonClicked(null);
    }
}
