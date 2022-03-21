package front.controllers;

import app.Main;
import back.user.Wallet;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
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
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;

public class RequestTransferPermissionSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton;
    @FXML
    public Label selectPortfolioLabel, noPortfolioSelectedLabel, requestNotSentLabel, requestSentLabel;
    @FXML
    public ComboBox<String> portfolioComboBox;
    @FXML
    public Button sendRequestButton;

    private boolean requestSent = false;

    private ObservableList<String> values;
    private ArrayList<Wallet> wallets; // TODO : FRIX : il faut mettre des trucs dans les wallets pour que le code marche :pensive:

    // TODO : Attention, il faut remplacer "Portfolio" par "Wallet". C'est une confusion de termes

    public void initialize() {
        values = FXCollections.observableArrayList(Arrays.asList("portfolio1", "portfolio2"));
        // TODO : back-end : fetch all available portfolios and put their name in the list
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
            portfolioComboBox.setValue("");
            requestSent = false;
        }
    }

    @FXML
    public void handleSendRequestButtonMouseClicked(MouseEvent event) {
        if (portfolioComboBox.getValue() != null && !portfolioComboBox.getValue().equals("")) {
            System.out.println("here");
            if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false);

                /*
            int a = 0;
            // Create the request and send it
            for (int i = 0; i < wallets.size(); i++) {
                if (wallets.get(i).getBank().getName().equals(portfolioComboBox.getValue())) {
                    a = i;
                }
            }
            Request request = new Request(Main.getUser(), wallets.get(a).getBank(), Reason.NEW_PORTFOLIO);
            request.send();
                 */

            fadeInAndOutNode(3000, requestSentLabel);
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
