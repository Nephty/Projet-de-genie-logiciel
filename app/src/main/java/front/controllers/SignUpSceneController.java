package front.controllers;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

public class SignUpSceneController extends Controller implements BackButtonNavigator, LanguageButtonNavigator {
    @FXML
    TextField confirmPasswordTextField, passwordTextField;
    @FXML
    CheckBox showHidePasswordCheckBox;
    @FXML
    Label SWIFTTakenLabel, nameTakenLabel, passwordDoesNotMatchLabel, languageNotChosenLabel,
            invalidCityLabel, invalidSWIFTLabel, invalidCountryLabel, invalidNameLabel;
    @FXML
    Button backButton, signUpButton;
    @FXML
    TextField cityField, SWIFTField, countryField, nameField;
    @FXML
    PasswordField passwordField, confirmPasswordField;
    @FXML
    Label favoriteLanguageLabel, signedUpLabel;
    @FXML
    ComboBox<String> languageComboBox;

    private boolean userSignedUp = false;

    public void initialize() {
        ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("EN_US", "FR_BE"));
        // TODO : back-end : fetch all available languages and put them in the list
        languageComboBox.setItems(values);

        passwordTextField.managedProperty().bind(showHidePasswordCheckBox.selectedProperty());
        passwordField.managedProperty().bind(showHidePasswordCheckBox.selectedProperty().not());
        confirmPasswordTextField.managedProperty().bind(showHidePasswordCheckBox.selectedProperty());
        confirmPasswordField.managedProperty().bind(showHidePasswordCheckBox.selectedProperty().not());
        // Set visible property : if the checkbox is not ticked, show the password field, else, show the password text field
        passwordTextField.visibleProperty().bind(showHidePasswordCheckBox.selectedProperty());
        passwordField.visibleProperty().bind(showHidePasswordCheckBox.selectedProperty().not());
        confirmPasswordTextField.visibleProperty().bind(showHidePasswordCheckBox.selectedProperty());
        confirmPasswordField.visibleProperty().bind(showHidePasswordCheckBox.selectedProperty().not());
        // Set selection property : if you type something in the password field, you also type it in the password text field
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        hideAllLabels();
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        if (userSignedUp) {
            // if the user signed up, clear the form
            // if he didn't sign up, we're saving the inputs
            languageComboBox.setValue(null);
            emptyAllTextFields();
            hideAllLabels();
            userSignedUp = false;
        }
    }

    @Override
    public void emulateBackButtonClicked() {
        handleBackButtonNavigation(null);
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
        if (userSignedUp) {
            // if the user signed up, clear the form
            // if he didn't sign up, we're saving the inputs
            languageComboBox.setValue(null);
            emptyAllTextFields();
            hideAllLabels();
            userSignedUp = false;
        }
    }

    @FXML
    void handleSignUpButtonClicked(MouseEvent mouseEvent) {
        signUp();
    }

    /**
     * Checks if every field is properly filled in. Initializes the sign up process.
     */
    public void signUp() {
        String city = cityField.getText(), SWIFT = SWIFTField.getText(),
                country = countryField.getText(), name = nameField.getText(), password = passwordField.getText(),
                passwordConfirmation = confirmPasswordField.getText(), chosenLanguage = languageComboBox.getValue();

        // Manage the "invalid xxxx" labels visibility
        // Is the city valid
        if (!isValidCity(city) && !invalidCityLabel.isVisible()) invalidCityLabel.setVisible(true);
        else if (isValidCity(city) && invalidCityLabel.isVisible()) invalidCityLabel.setVisible(false);
        // Is the SWIFT valid
        if (!isValidSWIFT(SWIFT) && !invalidSWIFTLabel.isVisible()) invalidSWIFTLabel.setVisible(true);
        else if (isValidSWIFT(SWIFT) && invalidSWIFTLabel.isVisible()) invalidSWIFTLabel.setVisible(false);
        // Is the country valid
        if (!isValidCountry(country) && !invalidCountryLabel.isVisible()) invalidCountryLabel.setVisible(true);
        else if (isValidCountry(country) && invalidCountryLabel.isVisible()) invalidCountryLabel.setVisible(false);
        // Is the name valid
        if (!isValidName(name) && !invalidNameLabel.isVisible()) invalidNameLabel.setVisible(true);
        else if (isValidName(name) && invalidNameLabel.isVisible()) invalidNameLabel.setVisible(false);


        // PRO TIP : if the name is invalid, it cannot be taken, so we can safely give the same layout (coordinates)
        // the labels "invalid name" and "name taken". If the label "invalid name" shows up, it is
        // impossible for the "name taken" label to show up and vice versa.

        // Manage the "password does not match" label visibility
        if (!passwordMatchesAndIsNotEmpty(password, passwordConfirmation) && !passwordDoesNotMatchLabel.isVisible())
            passwordDoesNotMatchLabel.setVisible(true);
        else if (passwordMatchesAndIsNotEmpty(password, passwordConfirmation) && passwordDoesNotMatchLabel.isVisible())
            passwordDoesNotMatchLabel.setVisible(false);

        if (chosenLanguage == null && !languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(true);
        else if (chosenLanguage != null && languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(false);


        // No label is visible implies that every field is properly filled in
        if (noLabelVisible()) {
            // Then we can create a new bank
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            try {
                response = Unirest.post("https://flns-spring-test.herokuapp.com/api/bank")
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"swift\": \"" + SWIFT + "\",\r\n    \"name\": \"" + name + "\",\r\n    \"password\": \"" + password + "\",\r\n    \"address\": \"" + city + "\",\r\n    \"country\": \"" + country + "\",\r\n    \"defaultCurrencyId\": 0\r\n}")
                        .asString();
                if(response.getStatus() != 403) {
                    Main.errorCheck(response.getStatus());
                }
            } catch (UnirestException e) {
                Main.errorCheck(408);
            }

            if (response != null) {
                if (response.getStatus() == 403) {
                    switch (response.getBody()) {
                        case "SWIFT":
                            SWIFTTakenLabel.setVisible(true);
                            break;
                        case "NAME":
                            nameTakenLabel.setVisible(true);
                            break;
                    }
                } else {
                    fadeInAndOutNode(3000, signedUpLabel);
                    userSignedUp = true;
                    // Empty all data that we don't need, it's a security detail
                    city = "";
                    SWIFT = "";
                    country = "";
                    name = "";
                    password = "";
                    passwordConfirmation = "";
                    chosenLanguage = "";
                }
            }
        }
    }

    /**
     * Checks if any label that show if any field is not properly filled in is visible. If any is visible,
     * the user didn't properly fill in every field. If none are visible, every field is properly filled in.
     * This is directly used to check if every field is properly filled in to begin the sign up process.
     *
     * @return <code>boolean</code> - whether any label is visible or not
     */
    private boolean noLabelVisible() {
        return !invalidCityLabel.isVisible() && !invalidSWIFTLabel.isVisible()
                && !invalidCountryLabel.isVisible() && !invalidNameLabel.isVisible()
                && !passwordDoesNotMatchLabel.isVisible() && !languageNotChosenLabel.isVisible();
    }

    @FXML
    void handleComponentKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    public void emulateSignUpButtonClicked() {
        handleSignUpButtonClicked(null);
    }

    /**
     * Hides all indicator labels (labels that tell the user if something is wrong with their input).
     */
    public void hideAllLabels() {
        SWIFTTakenLabel.setVisible(false);
        nameTakenLabel.setVisible(false);
        passwordDoesNotMatchLabel.setVisible(false);
        languageNotChosenLabel.setVisible(false);
        invalidCityLabel.setVisible(false);
        invalidSWIFTLabel.setVisible(false);
        invalidCountryLabel.setVisible(false);
        invalidNameLabel.setVisible(false);
    }

    /**
     * Removes all text entered in all text fields.
     */
    public void emptyAllTextFields() {
        SWIFTField.setText("");
        cityField.setText("");
        countryField.setText("");
        nameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
