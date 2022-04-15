package front.controllers;

import app.Main;
import back.user.Bank;
import back.user.CommunicationType;
import back.user.Request;
import back.user.Wallet;
import com.mashape.unirest.http.exceptions.UnirestException;
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
import java.util.ArrayList;

public class RequestNewPortfolioSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, sendRequestButton;
    @FXML
    Label selectSWIFTLabel, noSWIFTSelectedLabel, requestSentLabel, requestNotSentLabel; // Note : we don't use the last label
    @FXML
    ComboBox<String> SWIFTComboBox;

    private boolean requestSent = false;

    // TODO : Attention, il faut remplacer "Portfolio" par "Wallet". C'est une confusion de termes


    public void initialize() {
        Main.updatePortfolio();
        ObservableList<String> values = FXCollections.observableArrayList();
        values = FXCollections.observableArrayList(Bank.fetchAllSWIFT());
        ArrayList<String> alreadyOwnsAWalletSWIFTs = new ArrayList<>();
        for (Wallet w : Main.getPortfolio().getWalletList()) alreadyOwnsAWalletSWIFTs.add(w.getBank().getSwiftCode());
        values.removeIf(alreadyOwnsAWalletSWIFTs::contains);
        SWIFTComboBox.setDisable(values.size() == 0);
        SWIFTComboBox.setItems(values);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        SWIFTComboBox.setDisable(false);
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (requestSent) {
            SWIFTComboBox.setValue(SWIFTComboBox.getPromptText()); // TODO : this is not showing the prompt text
            requestSent = false;
        }
    }

    @FXML
    void handleSendRequestButton(MouseEvent event) throws UnirestException {
        if (SWIFTComboBox.getValue() != null && !SWIFTComboBox.getValue().equals("")) {
            if (noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(false);

            // Create the request and send it
            Request request = new Request(SWIFTComboBox.getValue(), CommunicationType.CREATE_ACCOUNT, Date.from(Instant.now()).toString(), "");
            request.send();

            fadeInAndOutNode(1000, requestSentLabel);
            requestSent = true;

            // Reset the form
            SWIFTComboBox.setValue("");
        } else if (!noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(true);
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
