package front.controllers;

import app.Main;
import back.user.Account;
import back.user.CommunicationType;
import back.user.Request;
import back.user.Wallet;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.sql.Date;
import java.time.Instant;

/**
 * @author Arnaud MOREAU
 */
public class RequestAccountRemovalSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, sendRequestButton;
    @FXML
    Label selectAccountLabel, noAccountSelectedLabel, requestSentLabel, requestNotSentLabel; // Note : we don't use the last label
    @FXML
    ComboBox<String> accountsComboBox;

    private boolean requestSent = false;

    public void initialize() {
        Main.updatePortfolio();
        ObservableList<String> values;
        values = FXCollections.observableArrayList();
        for (Wallet wallet : Main.getPortfolio().getWalletList()) {
            for (Account account : wallet.getAccountList()) {
                values.add(account.toString());
            }
        }
        accountsComboBox.setDisable(values.size() == 0);
        accountsComboBox.setItems(values);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        accountsComboBox.setDisable(false);
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (requestSent) {
            accountsComboBox.setValue(null);
            requestSent = false;
        }
    }

    @FXML
    void handleSendRequestButton(MouseEvent event) {
        if (accountsComboBox.getValue() != null && !accountsComboBox.getValue().equals("")) {
            if (noAccountSelectedLabel.isVisible()) noAccountSelectedLabel.setVisible(false);

            // Create the request and send it
            String IBAN = accountsComboBox.getValue().substring(16,32);
            String swift = accountsComboBox.getValue().substring(7,15);

            Request request = new Request(swift, CommunicationType.DELETE_ACCOUNT, Date.from(Instant.now()).toString(), IBAN);
            request.send();

            fadeInAndOutNode(1000, requestSentLabel);
            requestSent = true;

            // Reset the form
            accountsComboBox.setValue("");
        } else if (!noAccountSelectedLabel.isVisible()) noAccountSelectedLabel.setVisible(true);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }

    @FXML
    void handleSWIFTComboBoxMouseClicked(MouseEvent mouseEvent) {
        requestSent = false;
    }
}
