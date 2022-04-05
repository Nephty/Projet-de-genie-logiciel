package front.controllers;

import app.Main;
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

public class RequestTransferPermissionSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton;
    @FXML
    public Label selectPortfolioLabel, noPortfolioSelectedLabel, requestNotSentLabel, requestSentLabel;
    @FXML
    public ComboBox<Wallet> portfolioComboBox;
    @FXML
    public Button sendRequestButton;

    private boolean requestSent = false;

    private ObservableList<Wallet> values;
    private ArrayList<Wallet> wallets; // TODO : FRIX : il faut mettre des trucs dans les wallets pour que le code marche :pensive:

    // TODO : Attention, il faut remplacer "Portfolio" par "Wallet". C'est une confusion de termes

    public void initialize() {
        Main.updatePortfolio();
        values = FXCollections.observableArrayList(Main.getPortfolio().getWalletList());
        portfolioComboBox.setItems(values);
    }

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
        if (requestSent) {
            portfolioComboBox.setValue(null);
            requestSent = false;
        }
    }

    @FXML
    public void handleSendRequestButtonMouseClicked(MouseEvent event) {
        if (portfolioComboBox.getValue() != null) {
            if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false);

            // Create the request and send it
            Request request = new Request(portfolioComboBox.getValue().getBank().getSwiftCode(), CommunicationType.NEW_PORTFOLIO, "");
            request.send();

            fadeInAndOutNode(1000, requestSentLabel);
            requestSent = true;

            // Reset the form
            portfolioComboBox.getSelectionModel().clearSelection();
        } else if (!noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(true);
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }

    @FXML
    public void handlePortfolioComboBoxMouseCLicked(MouseEvent mouseEvent) {
        requestSent = false;
    }
}
