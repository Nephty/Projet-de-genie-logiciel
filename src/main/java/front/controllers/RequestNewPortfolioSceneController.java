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

public class RequestNewPortfolioSceneController extends Controller implements BackButtonNavigator {
    @FXML
    public Button backButton, sendRequestButton;
    @FXML
    public Label selectSWIFTLabel, noSWIFTSelectedLabel, requestSentLabel, requestNotSentLabel; // Note : we don't use the last label
    @FXML
    public ComboBox<String> SWIFTComboBox;

    private boolean requestSent = false;

    public void initialize() {
        ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("AAAABEBB000", "HHHHBEBB999"));
        // TODO : back-end : fetch all available SWIFT codes and put them in the list
        SWIFTComboBox.setItems(values);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(false); // This can be done everytime
        if (requestSent) {
            // If the request was sent, reset the form
            if (requestSentLabel.isVisible()) requestSentLabel.setVisible(false);
            SWIFTComboBox.setValue(SWIFTComboBox.getPromptText()); // TODO : this is not showing the prompt text
            requestSent = false;
        }
    }

    @FXML
    public void handleSendRequestButton(MouseEvent event) {
        if (SWIFTComboBox.getValue() != null) {
            if (noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(false);

            // TODO : back-end : send request to database

            requestSentLabel.setVisible(true);
            requestSent = true;
        } else if (!noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(true);
    }
}
