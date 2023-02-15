package front.controllers;

import app.Main;
import back.user.Wallet;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Arnaud MOREAU
 */
public class FinancialProductsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, fetchProductsButton, detailsButton;
    @FXML
    Label lastUpdateTimeLabel, loadingProductsLabel;
    @FXML
    TableView<Wallet> productsTableView;
    @FXML
    TableColumn<Wallet, String> bankNameColumn, bankSWIFTColumn, accountsColumn;

    public void initialize() {
        bankNameColumn.setCellValueFactory(w -> new SimpleStringProperty(w.getValue().getBank().getName()));
        bankSWIFTColumn.setCellValueFactory(w -> new SimpleStringProperty(w.getValue().getBank().getSwiftCode()));
        accountsColumn.setCellValueFactory(w -> new SimpleStringProperty(String.valueOf(w.getValue().getNumberOfAccounts())));
        productsTableView.setPlaceholder(new Label("No products available."));
        productsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fetchProducts();
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
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    void handleDetailsButtonClicked(MouseEvent event) {
        // If the user selected one wallet
        if (productsTableView.getSelectionModel().getSelectedItems().size() == 1) {
            Main.setCurrentWallet(productsTableView.getSelectionModel().getSelectedItems().get(0));
            Scenes.ProductDetailsScene = SceneLoader.load("ProductDetailsScene.fxml", Main.appLocale);
            Main.setScene(Flow.forward(Scenes.ProductDetailsScene));
        }
    }

    @FXML
    void handleFetchProductButtonClicked(MouseEvent event) {
        updateProducts();
    }

    /**
     * Updates the products
     */
    public void updateProducts() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc.)
        if (loadingProductsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingNotificationsLabelFadeThread;
            // Fade the label "updating product..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingProductsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingNotificationsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Update the portfolio
            Main.updatePortfolio();
            // Fetch wallets from the updated portfolio
            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();

            // Fade the label "updating products..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeInDuration, sleepDuration + fadeInDuration, loadingProductsLabel);
            // Put the wallets in listView
            productsTableView.setItems(FXCollections.observableArrayList(walletList));
        }
    }


    /**
     * Fetches wallets from the database to display them
     */
    public void fetchProducts() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc.)
        if (loadingProductsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingNotificationsLabelFadeThread;
            // Fade the label "updating products..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingProductsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingNotificationsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch wallets from the portfolio
            ArrayList<Wallet> walletList = Main.getPortfolio().getWalletList();

            // Fade the label "updating products..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeInDuration, sleepDuration + fadeInDuration, loadingProductsLabel);
            // Put the wallets in the listView
            productsTableView.setItems(FXCollections.observableArrayList(walletList));
        }
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
