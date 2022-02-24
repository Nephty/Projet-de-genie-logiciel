package front.controllers;

import BenkyngApp.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

public class RequestTransferPermissionSceneController implements BackButtonNavigator {
    @FXML
    public Button backButton;
    @FXML
    public Label selectPortfolioLabel, noPortfolioSelectedLabel, requestNotSentLabel, requestSentLabel;
    @FXML
    public ComboBox<String> portfolioComboBox;
    @FXML
    public Button sendRequestButton;

    private boolean portfolioComboBoxInitialized = false, requestSent = false;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        portfolioComboBoxInitialized = false; // So we can reload the content next time we open this scene
        if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false); // This can be done everytime
        if (requestSent) {
            // If the request was sent, reset the form
            if (requestSentLabel.isVisible()) requestSentLabel.setVisible(false);
            portfolioComboBox.setValue(portfolioComboBox.getPromptText()); // TODO : this is not showing the prompt text
            requestSent = false;
        }
    }

    @FXML
    public void handlePortfolioComboBoxMouseClicked(MouseEvent event) {
        initializePortfolioComboBox();
    }

    @FXML
    public void handleSendRequestButton(MouseEvent event) {
        if (portfolioComboBox.getValue() != null) {
            if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false);

            // TODO : back-end : send request to database

            requestSentLabel.setVisible(true);
            requestSent = true;
        } else if (!noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(true);
    }

    /**
     * Initializes the portfolios combo box : retrieves all available portfolios and present them as choices in the
     * combo box.
     * Note : this method is only ran if <code>PortfolioComboBoxInitialized</code> is false. When ran, it turns this
     * variable to true, which allows it to only be run once, namely when we first click the combo box.
     */
    public void initializePortfolioComboBox() {
        if (!portfolioComboBoxInitialized) {
            ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("portfolio1", "portfolio2"));
            // TODO : back-end : fetch all available portfolios and put them in the list
            portfolioComboBox.setItems(values);
            portfolioComboBoxInitialized = true;
        }
    }
}
