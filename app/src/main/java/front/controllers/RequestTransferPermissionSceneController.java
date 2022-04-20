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

import java.util.ArrayList;

/**
 * @author Arnaud MOREAU
 */
public class RequestTransferPermissionSceneController extends Controller implements BackButtonNavigator {

    @FXML
    Button backButton;

    @FXML
    Label selectPortfolioLabel, noPortfolioSelectedLabel, requestNotSentLabel, requestSentLabel;

    @FXML
    ComboBox<Account> portfolioComboBox;

    @FXML
    Button sendRequestButton;

    private boolean requestSent = false;

    private ObservableList<Account> values;

    public void initialize() {
        // Update the Portfolio
        Main.updatePortfolio();
        ArrayList<Account> accountList = new ArrayList<>();
        // Creates a list with all the account without transfer permission
        for (Wallet wallet : Main.getPortfolio().getWalletList()) {
            for (Account account : wallet.getAccountList()) if (!account.canPay()) accountList.add(account);
        }
        values = FXCollections.observableArrayList(accountList);
        portfolioComboBox.setDisable(values.size() == 0);
        portfolioComboBox.setItems(values);
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
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        portfolioComboBox.setDisable(false);
        if (requestSent) {
            portfolioComboBox.setValue(null);
            requestSent = false;
        }
    }


    @FXML
    void handleSendRequestButtonMouseClicked(MouseEvent event) {
        if (portfolioComboBox.getValue() != null) {
            if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false);

            // Create the request and send it
            Request request = new Request(portfolioComboBox.getValue().getBank().getSwiftCode(), CommunicationType.TRANSFER_PERMISSION, "", portfolioComboBox.getValue().getIBAN());
            request.send();

            fadeInAndOutNode(1000, requestSentLabel);
            requestSent = true;

            // Reset the form
            portfolioComboBox.getSelectionModel().clearSelection();
        } else if (!noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(true);
    }


    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }


    @FXML
    void handlePortfolioComboBoxMouseCLicked(MouseEvent mouseEvent) {
        requestSent = false;
    }
}
