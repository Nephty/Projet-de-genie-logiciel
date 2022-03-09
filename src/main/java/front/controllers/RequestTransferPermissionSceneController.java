package front.controllers;

import BenkyngApp.Main;
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
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

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

    public void initialize() {
        ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("portfolio1", "portfolio2"));
        // TODO : back-end : fetch all available portfolios and put them in the list
        portfolioComboBox.setItems(values);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false); // This can be done everytime
        if (requestSent) {
            portfolioComboBox.setValue(portfolioComboBox.getPromptText()); // TODO : this is not showing the prompt text
            requestSent = false;
        }
    }

    @FXML
    public void handleSendRequestButton(MouseEvent event) {
        if (portfolioComboBox.getValue() != null) {
            if (noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(false);

            // TODO : back-end : send request to database

            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 3000;
            FadeOutThread sleepAndFadeOutRequestSentLabelFadeThread;
            FadeInTransition.playFromStartOn(requestSentLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutRequestSentLabelFadeThread = new FadeOutThread();
            sleepAndFadeOutRequestSentLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, requestSentLabel);
            requestSent = true;
        } else if (!noPortfolioSelectedLabel.isVisible()) noPortfolioSelectedLabel.setVisible(true);
    }
}
