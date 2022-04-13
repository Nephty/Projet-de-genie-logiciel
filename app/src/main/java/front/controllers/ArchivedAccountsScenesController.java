package front.controllers;

import app.Main;
import back.user.Account;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class ArchivedAccountsScenesController extends Controller implements BackButtonNavigator {
    @FXML
    TableColumn<Account, String> bankNameColumn, IBANColumn, accountTypeColumn, transferPermissionsColumn, subAccountsColumn, activatedColumn;
    @FXML
    Label lastUpdateTimeLabel, loadingAccountsLabel;
    @FXML
    Button backButton, fetchAccountsButton, restoreButton;
    @FXML
    TableView<Account> archivedAccountsTableView;

    ObservableList<Account> data = FXCollections.observableArrayList();

    public void initialize() {
        archivedAccountsTableView.setPlaceholder(new Label("No archived account."));
        bankNameColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getBank().getName()));
        IBANColumn.setCellValueFactory(new PropertyValueFactory<>("IBAN"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        transferPermissionsColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().canPay() ? "Yes" : "No"));
        subAccountsColumn.setCellValueFactory(a -> new SimpleStringProperty(String.valueOf(a.getValue().getSubAccountList().size())));
        activatedColumn.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().isActivated() ? "Yes" : "No"));
        archivedAccountsTableView.setPlaceholder(new Label("No account available."));
        archivedAccountsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        updateArchivedAccounts();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent mouseEvent) {
        System.out.println("before Back AAS - Flow.getContentAsString() = " + Flow.getContentAsString());
        Main.setScene(Flow.back());
        System.out.println("after Back AAS - Flow.getContentAsString() = " + Flow.getContentAsString());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        Scenes.ProductDetailsScene = SceneLoader.load("ProductDetailsScene.fxml", Main.appLocale);
        Flow.replaceBeforeLastElement(Scenes.ProductDetailsScene);
        handleBackButtonNavigation(event);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        }
    }

    public void handleRestoreButtonClicked(MouseEvent mouseEvent) {
        if (archivedAccountsTableView.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList<Account> selection = archivedAccountsTableView.getSelectionModel().getSelectedItems();
            try {
                for (Account account : selection) {
                    restoreAccount(account);
                    data.remove(account);
                }
            } catch (NoSuchElementException e) {
                // This exception occurs when the user archives the last visible account remaining
                // The reason is that JavaFX instantly updates the selection when an item is removed,
                // so the selection becomes empty, throwing a NoSuchElementException
            }
            archivedAccountsTableView.setItems(data);
        }
    }

    public void handleFetchAccountsButtonClicked(MouseEvent mouseEvent) {
        updateArchivedAccounts();
    }

    /**
     * Updates the archived accounts
     */
    public void updateArchivedAccounts() {
        // TODO : this should be a "fetch" method and use a request on the API to synchronize to the DB
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingAccountsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingAccountsLabelFadeThread;
            // Fade the label "updating accounts..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingAccountsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingAccountsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Update the portfolio and then fetch the accounts
            // Main.updatePortfolio();
//            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();
//            ArrayList<Account> accountsList = new ArrayList<>();
//            for (Wallet wallet : walletList) {
//                for (Account account : wallet.getAccountList()) {
//                    if (account.isArchived()) accountsList.add(account);
//                }
//            }
            Main.getCurrentWallet().fetchArchivedAccount();
            ArrayList<Account> accountsList = Main.getCurrentWallet().getArchivedAccountList();
            // Put the accounts in the listview
            data = FXCollections.observableArrayList(accountsList);
            archivedAccountsTableView.setItems(data);
            // Fade the label "updating accounts..." out to 0.0 opacity
            sleepAndFadeOutLoadingAccountsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingAccountsLabel);
        }
    }

    private void restoreAccount(Account account) {
        account.setArchived(false);
        // TODO : set archived to false in database
    }
}
