package front.controllers;

import BenkyngApp.Main;
import back.user.Account;
import back.user.Transaction;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Calendar;
import java.util.Currency;

public class TransactionsHistorySceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, exportButton, fetchTransactionsHistoryButton;
    @FXML
    public ListView<Transaction> transactionsHistoryListView;
    @FXML
    public Label lastUpdateTimeLabel, loadingTransactionsHistoryLabel;

    public void initialize() {
        fetchTransactions();
        transactionsHistoryListView.setItems(FXCollections.observableArrayList(
                new Transaction(69, "user A", "user B", 1000f, Currency.getInstance("USD")),
                new Transaction(420, "user B", "user A", 0.01f, Currency.getInstance("EUR"))));
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    public void handleExportButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.ExportHistoryScene));
    }

    @FXML
    public void handleFetchTransactionsHistoryButtonClicked(MouseEvent event) {
        fetchTransactions();
    }

    public void fetchTransactions() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingTransactionsHistoryLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingTransactionsLabelFadeThread;
            // Fade the label "updating history..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingTransactionsHistoryLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingTransactionsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch transactions and put them in the listview
            // TODO : back-end : fetch transactions from the database and put them in the listview
            // Fade the label "updating history..." out to 0.0 opacity
            sleepAndFadeOutLoadingTransactionsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingTransactionsHistoryLabel);
        }
    }
}
