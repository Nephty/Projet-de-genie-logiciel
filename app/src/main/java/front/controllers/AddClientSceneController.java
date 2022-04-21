package front.controllers;

import app.Main;
import back.user.Profile;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.scenes.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class AddClientSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, addClientButton;
    @FXML
    Label enterNRNLabel, invalidNRNLabel, requestNotSentLabel, clientAddedLabel;
    @FXML
    TextField NRNTextField;

    private boolean clientAdded = false;

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateAddClientButtonClicked();
            keyEvent.consume();
        }
    }

    @FXML
    void handleAddClientButtonClicked(MouseEvent event) {
        String NRN = NRNTextField.getText();

        if (!isValidNRN(NRN) && !invalidNRNLabel.isVisible()) invalidNRNLabel.setVisible(true);
        else if (isValidNRN(NRN) && invalidNRNLabel.isVisible()) invalidNRNLabel.setVisible(false);

        if (!invalidNRNLabel.isVisible()) {
            // Check if the client already exists
            ArrayList<Profile> customersList = Profile.fetchAllCustomers(Main.getBank().getSwiftCode());
            boolean alreadyExist = false;
            for (Profile profile : customersList) {
                if (profile.getNationalRegistrationNumber().equals(NRN)) {
                    alreadyExist = true;
                    break;
                }
            }
            if (!alreadyExist) {
                // Create a account to create a client
                Main.setNewClient(NRN);
                Main.setScene(Flow.forward(Scenes.CreateClientAccountScene));
                fadeInAndOutNode(3000, clientAddedLabel);
                clientAdded = true;

                // Reset the form
                NRNTextField.setText("");
            } else {
                // TODO : Afficher une erreur
            }

        }
    }

    private void emulateAddClientButtonClicked() {
        handleAddClientButtonClicked(null);
    }

    @FXML
    void handleNRNTextFieldKeyPressed(KeyEvent keyEvent) {
        clientAdded = false;
    }
}
