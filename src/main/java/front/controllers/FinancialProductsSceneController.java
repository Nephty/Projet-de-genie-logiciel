package front.controllers;

import BenkyngApp.Main;
import back.user.Wallet;
import front.animation.FadeInTransition;
import front.animation.threads.FadeOutThread;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.Calendar;

public class FinancialProductsSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, fetchProductsButton, detailsButton, visualizeToolButton;
    @FXML
    public Label lastUpdateTimeLabel, loadingProductsLabel;
    @FXML
    public ListView<Wallet> productsListView;

    private Wallet selectedWallet;

    public void initialize() {
        // TODO : back-end : fetch wallets from the database and put them in the listview
        fetchProducts();
//        productsListView.setItems(FXCollections.observableArrayList(new Wallet("wallet A"), new Wallet("wallet B")));
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
    public void handleDetailsButtonClicked(MouseEvent event) {
        // If the user selected one wallet
        if (productsListView.getSelectionModel().getSelectedItems().size() == 1) {
            Main.setScene(Flow.forward(Scenes.ProductDetailsScene));
            // TODO : forward the selected account to the product detail scene
        }
    }

    @FXML
    public void handleFetchProductButtonClicked(MouseEvent event) {
        fetchProducts();
    }

    /**
     * Fetches wallets from the database to display them
     */
    public void fetchProducts() {
        // Execute this only if the label is not visible (that is, only if we are not already retrieving data etc)
        if (loadingProductsLabel.getOpacity() == 0.0) {
            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 1000;
            FadeOutThread sleepAndFadeOutLoadingNotificationsLabelFadeThread;
            // Fade the label "updating notifications..." in to 1.0 opacity
            FadeInTransition.playFromStartOn(loadingProductsLabel, Duration.millis(fadeInDuration));
            // We use a new Thread, so we can sleep the method for a few hundreds of milliseconds so that the label
            // doesn't instantly go away when the notifications are retrieved.
            sleepAndFadeOutLoadingNotificationsLabelFadeThread = new FadeOutThread();
            // Save actual time and date
            Calendar c = Calendar.getInstance();
            // Update lastUpdateLabel with the new time and date
            lastUpdateTimeLabel.setText("Last update : " + formatCurrentTime(c));
            // Fetch notifications and put them in the listview
            // TODO : back-end : fetch products from the database and put them in the listview
            // Fade the label "updating notifications..." out to 0.0 opacity
            sleepAndFadeOutLoadingNotificationsLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, loadingProductsLabel);
        }
    }

    public void handleVisualizeToolButtonClicked(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.VisualizeToolScene));
    }
}
