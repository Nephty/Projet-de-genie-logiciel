package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
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
    }

    @FXML
    public void handleButtonKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    public void handleButton1MouseClicked(MouseEvent event) {
        System.out.println(event.getSource().toString().substring(38, 39));
    }

    @FXML
    public void handleButton2MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton3MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton4MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton5MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton6MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton7MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton8MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton9MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButton0MouseClicked(MouseEvent event) {
    }

    @FXML
    public void handleButtonDelMouseClicked(MouseEvent event) {
    }
}
