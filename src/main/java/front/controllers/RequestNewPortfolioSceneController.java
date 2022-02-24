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

public class RequestNewPortfolioSceneController implements BackButtonNavigator {
    @FXML
    public Button backButton, sendRequestButton;
    @FXML
    public Label selectSWIFTLabel, noSWIFTSelectedLabel, requestNotSentLabel; // Note : we don't use the last label
    @FXML
    public ComboBox<String> SWIFTComboBox;

    private boolean SWIFTComboBoxInitialized = false;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @FXML
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        SWIFTComboBoxInitialized = false; // So we can reload the content next time we open this scene
    }

    @FXML
    public void handleSendRequestButton(MouseEvent event) {
        if (SWIFTComboBox.getValue() != null) {
            if (noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(false);

            // TODO : back-end : send request to database

        } else if (!noSWIFTSelectedLabel.isVisible()) noSWIFTSelectedLabel.setVisible(true);
    }

    /**
     * Initializes the SWIFT combo box : retrieves all available SWIFT codes and present them as choices in the
     * combo box.
     * Note : this method is only ran if <code>SWIFTComboBoxInitialized</code> is false. When ran, it turns this
     * variable to true, which allows it to only be run once, namely when we first click the combo box.
     */
    public void initializeLanguageComboBox() {
        if (!SWIFTComboBoxInitialized) {
            ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("AAAABEBB000", "HHHHBEBB999"));
            // TODO : back-end : fetch all available SWIFT codes and put them in the list
            SWIFTComboBox.setItems(values);
            SWIFTComboBoxInitialized = true;
        }
    }

    public void handleSWIFTComboBoxMouseClicked(MouseEvent event) {
        initializeLanguageComboBox();
    }
}
