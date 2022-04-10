package front.controllers;

import app.Main;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionsHistorySceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, exportButton, fetchTransactionsHistoryButton;
    @FXML
    public TableView<Transaction> transactionsHistoryTableView;
    @FXML
    public TableColumn<Transaction, String> senderNameColumn, senderIBANColumn, receiverNameColumn, receiverIBANColumn, dateColumn, amountColumn;
    @FXML
    public Label lastUpdateTimeLabel, loadingTransactionsHistoryLabel;

    public void initialize() {
        senderNameColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        senderIBANColumn.setCellValueFactory(new PropertyValueFactory<>("senderIBAN"));
        receiverNameColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        receiverIBANColumn.setCellValueFactory(new PropertyValueFactory<>("receiverIBAN"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sendingDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        fetchTransactions();
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
    }

    @FXML
    public void handleExportButtonClicked(MouseEvent event) {
        if (transactionsHistoryTableView.getSelectionModel().getSelectedItems().size() <= 1) {
            // Export all transactions
            ExportHistorySceneController.setExportData(new ArrayList<>(transactionsHistoryTableView.getItems()));
        } else {
            // Export selected data
            ExportHistorySceneController.setExportData(new ArrayList<>(transactionsHistoryTableView.getSelectionModel().getSelectedItems()));
        }
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
            // Fade the label "updating history..." out to 0.0 opacity
            ArrayList<Transaction> transactionList = Main.getCurrentAccount().getSubAccountList().get(0).getTransactionHistory();

            ArrayList<String> strList = new ArrayList<String>();

            transactionsHistoryTableView.setItems(FXCollections.observableArrayList(transactionList));
            sleepAndFadeOutLoadingTransactionsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingTransactionsHistoryLabel);
        }
    }

    @FXML
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
