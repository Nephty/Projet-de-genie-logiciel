package front.controllers;

import app.Main;
import back.user.AccountType;
import back.user.ErrorHandler;
import back.user.Request;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CreateClientAccountSceneController extends Controller implements BackButtonNavigator {
    @FXML
    Button backButton, createAccountButton;
    @FXML
    Label enterIBANLabel, accountCreatedLabel, chooseAccountTypeLabel, invalidIBANLabel, noValueSelectedLabel, invalidCoOwner1NRN, invalidCoOwner2NRN;
    @FXML
    TextField IBANTextField, coOwner1TextField, coOwner2TextField;
    @FXML
    ComboBox<AccountType> accountTypeComboBox;

    private boolean accountCreated = false;

    public void initialize() {
        accountTypeComboBox.setItems(FXCollections.observableArrayList(AccountType.COURANT, AccountType.EPARGNE, AccountType.TERME, AccountType.JEUNE));
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        if (accountCreated) {
            IBANTextField.setText("");
            accountTypeComboBox.valueProperty().set(null);
            accountCreated = false;
        }
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonClicked(null);
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            keyEvent.consume();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateCreateAccountButtonClicked();
            keyEvent.consume();
        }
    }

    private void emulateCreateAccountButtonClicked() {
        handleCreateAccountButtonClicked(null);
    }

    @FXML
    void handleCreateAccountButtonClicked(MouseEvent event) {
        String IBAN = IBANTextField.getText(), coOwner1 = coOwner1TextField.getText(), coOwner2 = coOwner2TextField.getText();

        if (!invalidIBANLabel.isVisible() && !isValidIBAN(IBAN)) invalidIBANLabel.setVisible(true);
        else if (invalidIBANLabel.isVisible() && isValidIBAN(IBAN)) invalidIBANLabel.setVisible(false);
        if (!invalidCoOwner1NRN.isVisible() && !isValidNRN(coOwner1)) invalidCoOwner1NRN.setVisible(true);
        else if (invalidCoOwner1NRN.isVisible() && isValidNRN(coOwner1)) invalidCoOwner1NRN.setVisible(false);
        if (!invalidCoOwner2NRN.isVisible() && !isValidNRN(coOwner2)) invalidCoOwner2NRN.setVisible(true);
        else if (invalidCoOwner2NRN.isVisible() && isValidNRN(coOwner2)) invalidCoOwner2NRN.setVisible(false);

        if (!noValueSelectedLabel.isVisible() && accountTypeComboBox.getValue() == null)
                noValueSelectedLabel.setVisible(true);
        if (noValueSelectedLabel.isVisible() && accountTypeComboBox.getValue() != null)
            noValueSelectedLabel.setVisible(false);

        AccountType accountType = accountTypeComboBox.getValue();
        int repType = 0;
        switch (accountType) {
            case COURANT:
                repType = 1;
                break;
            case JEUNE:
                repType = 2;
                break;
            case EPARGNE:
                repType = 3;
                break;
            case TERME:
                repType = 4;
                break;
        }

        if ( !invalidIBANLabel.isVisible() && !noValueSelectedLabel.isVisible()  && !invalidCoOwner1NRN.isVisible() && !invalidCoOwner2NRN.isVisible()) {
            // Creates account with all the values
            String swift = Main.getBank().getSwiftCode();
            String userId;
            // The userId depends on the context where this class is called
            if (Main.getNewClient() != null) {
                userId = Main.getNewClient();
            } else {
                userId = Main.getCurrentWallet().getAccountUser().getNationalRegistrationNumber();
            }
            Unirest.setTimeouts(0, 0);
            int finalRepType = repType;
            HttpResponse<String> response = ErrorHandler.handlePossibleError(() -> {
                HttpResponse<String> rep = null;
                try {
                    rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account")
                            .header("Authorization", "Bearer " + Main.getToken())
                            .header("Content-Type", "application/json")
                            .body("{\r\n    \"iban\": \"" + IBAN + "\",\r\n    \"swift\": \"" + swift + "\",\r\n    \"userId\": \"" + userId + "\",\r\n    \"accountTypeId\": " + finalRepType + "\r\n}")
                            .asString();
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
                return rep;
            });
            // TODO : A vérifier
            if (response.getStatus() == 409) {
                if (response.getBody().equals("IBAN")) {
                    invalidIBANLabel.setVisible(true);
                }
            } else {
                if(!coOwner1.equals("")){
                    // Create account access for the co-owner
                    Unirest.setTimeouts(0, 0);
                    HttpResponse<String> response3 = ErrorHandler.handlePossibleError(() -> {
                        HttpResponse<String> rep = null;
                        try {
                            rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account-access")
                                    .header("Authorization", "Bearer " + Main.getToken())
                                    .header("Content-Type", "application/json")
                                    .body("{\r\n    \"accountId\": \"" + IBAN + "\",\r\n    \"userId\": \"" + coOwner1 + "\",\r\n    \"access\": true,\r\n    \"hidden\": false\r\n}")
                                    .asString();
                        } catch (UnirestException e) {
                            throw new RuntimeException(e);
                        }
                        return rep;
                    });
                }

                if(!coOwner2.equals("")){
                    // Create account access for the co-owner
                    Unirest.setTimeouts(0, 0);
                    HttpResponse<String> response4 = ErrorHandler.handlePossibleError(() -> {
                        HttpResponse<String> rep = null;
                        try {
                            rep = Unirest.post("https://flns-spring-test.herokuapp.com/api/account-access")
                                    .header("Authorization", "Bearer " + Main.getToken())
                                    .header("Content-Type", "application/json")
                                    .body("{\r\n    \"accountId\": \"" + IBAN + "\",\r\n    \"userId\": \"" + coOwner2 + "\",\r\n    \"access\": true,\r\n    \"hidden\": false\r\n}")
                                    .asString();
                        } catch (UnirestException e) {
                            throw new RuntimeException(e);
                        }
                        return rep;
                    });
                }

                Main.setNewClient(null);

                if (Main.getRequest() != null) {
                    Request request = Main.getRequest();

                    if (response.getStatus() == 201) {
                        // Send a notification to the client
                        request.sendNotif("has created you an new account");

                        // Delete this request
                        Unirest.setTimeouts(0, 0);
                        HttpResponse<String> response3 = ErrorHandler.handlePossibleError(() -> {
                            HttpResponse<String> rep = null;
                            try {
                                rep = Unirest.delete("https://flns-spring-test.herokuapp.com/api/notification/" + request.getID())
                                        .header("Authorization", "Bearer " + Main.getToken())
                                        .asString();
                            } catch (UnirestException e) {
                                throw new RuntimeException(e);
                            }
                            return rep;
                        });
                    }
                }

                Main.setRequest(null);

                fadeInAndOutNode(1000, accountCreatedLabel);
                accountCreated = true;

                // Reset the form
                IBANTextField.setText("");
                accountTypeComboBox.valueProperty().set(null);
            }
        }
    }

    @FXML
    void handleIBANTextFieldKeyPressed() {
        accountCreated = false;
    }

    @FXML
    void handleAccountTypeComboBoxKeyPressed() {
        accountCreated = false;
    }

    @FXML
    void handleCoOwnerTextFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            emulateCreateAccountButtonClicked();
        }
        keyEvent.consume();
    }
}
