package front.controllers;

import app.Main;
import back.user.Bank;
import back.user.Reason;
import back.user.Request;
import com.mashape.unirest.http.exceptions.UnirestException;
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

public class RequestNewPortfolioSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, sendRequestButton;
    @FXML
    public Label selectSWIFTLabel, noSWIFTSelectedLabel, requestSentLabel, requestNotSentLabel; // Note : we don't use the last label
    @FXML
    public ComboBox<String> SWIFTComboBox;

    private boolean requestSent = false;

    // TODO : Attention, il faut remplacer "Portfolio" par "Wallet". C'est une confusion de termes


    public void initialize() {

        ObservableList<String> values = null;
        try {
            values = FXCollections.observableArrayList(Bank.fetchAllSWIFT());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        // TODO : back-end : fetch all available SWIFT codes and put them in the list
        SWIFTComboBox.setItems(values);
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
        if (noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(false); // This can be done everytime
        if (requestSent) {
            SWIFTComboBox.setValue(SWIFTComboBox.getPromptText()); // TODO : this is not showing the prompt text
            requestSent = false;
        }
    }

    @FXML
    public void handleSendRequestButton(MouseEvent event) throws UnirestException {
        if (SWIFTComboBox.getValue() != null) {
            if (noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(false);

            // Create the request and send it
            Request request = new Request(Main.getUser(), new Bank(SWIFTComboBox.getValue()), Reason.NEW_PORTFOLIO);
            request.send();

            requestSent = true;

            int fadeInDuration = 1000;
            int fadeOutDuration = fadeInDuration;
            int sleepDuration = 3000;
            FadeOutThread sleepAndFadeOutRequestSentLabelFadeThread;
            FadeInTransition.playFromStartOn(requestSentLabel, Duration.millis(fadeInDuration));
            sleepAndFadeOutRequestSentLabelFadeThread = new FadeOutThread();
            sleepAndFadeOutRequestSentLabelFadeThread.start(fadeOutDuration, sleepDuration + fadeInDuration, requestSentLabel);
            requestSent = true;
        } else if (!noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(true);
    }

    @FXML
    public void handleButtonKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
