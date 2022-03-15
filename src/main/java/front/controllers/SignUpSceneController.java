package front.controllers;

import app.Main;
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
    Button backButton, languageButton, signUpButton;
    @FXML
    TextField cityField, SWIFTField, countryField, nameField;
    @FXML
    PasswordField passwordField, confirmPasswordField;
    @FXML
    Label favoriteLanguageLabel, signedUpLabel;
    @FXML
    ComboBox<String> languageComboBox;

    private boolean userSignedUp = false;

    /**
     * Checks if the given <code>String</code> is a valid city.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z and from A-Z or a dash (-) or a space
     *
     * @param city - <code>String</code> - the city to check
     * @return <code>boolean</code> - whether the given city is a valid city or not
     */
    public static boolean isValidCity(String city) {
        if (city == null) return false;
        return (!city.equals("") && (city.matches("^[a-zA-Z- ]*$")));
    }

    /**
     * Checks if the given <code>String</code> is a valid SWIFT.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z, from A-Z or a dash (-).
     *
     * @param SWIFT - <code>String</code> - the SWIFT to check
     * @return <code>boolean</code> - whether the given SWIFT is a valid SWIFT or not
     */
    public static boolean isValidSWIFT(String SWIFT) {
        if (SWIFT.length() != 8) return false;
        for (int i = 0; i < SWIFT.length(); i++) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    if (!Character.isAlphabetic(SWIFT.charAt(i))) return false;
                    break;
                case 6:
                case 7:
                    if (!(Character.isAlphabetic(SWIFT.charAt(i)) || Character.isDigit(SWIFT.charAt(i)))) return false;
                    break;
            }
        }
        return true;
    }

    /**
     * Checks if the given string is a valid country.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z and from A-Z or a dash (-) or a space
     *
     * @param country - <code>String</code> - the country to check
     * @return <code>boolean</code> whether the given country is a valid country or not
     */
    public static boolean isValidCountry(String country) {
        return isValidCity(country);
    }

    /**
     * Checks if the given string is valid name.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z and from A-Z or a dash (-)
     *
     * @param name - <code>String</code> - the name to check
     * @return whether the given name is a valid name or not
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        return (!name.equals("") && (name.matches("^[a-zA-Z-]*$")));
    }

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
    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
        hideAllLabels();
    }

    @FXML
    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
        if (userSignedUp) {
            // if the user signed up, clear the form
            // if he didn't sign up, we're saving the inputs
            languageComboBox.setValue(null);
            signedUpLabel.setVisible(false);
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
            signedUpLabel.setVisible(false);
            emptyAllTextFields();
            hideAllLabels();
            userSignedUp = false;
        }
    }

    @FXML
    public void handleSignUpButtonClicked(MouseEvent mouseEvent) {
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


        // Manage the "xxxx already taken" labels visibility
        // Is the name already taken ?
        if (isNameTaken(name) && !nameTakenLabel.isVisible()) nameTakenLabel.setVisible(true);
        else if (!isNameTaken(name) && nameTakenLabel.isVisible()) nameTakenLabel.setVisible(false);
        // Is the SWIFT already taken ?
        if (isSWIFTTaken(SWIFT) && !SWIFTTakenLabel.isVisible()) SWIFTTakenLabel.setVisible(true);
        else if (!isSWIFTTaken(SWIFT) && SWIFTTakenLabel.isVisible()) SWIFTTakenLabel.setVisible(false);

        // Manage the "password does not match" label visibility
        if (!passwordMatchesAndIsNotEmpty(password, passwordConfirmation) && !passwordDoesNotMatchLabel.isVisible())
            passwordDoesNotMatchLabel.setVisible(true);
        else if (passwordMatchesAndIsNotEmpty(password, passwordConfirmation) && passwordDoesNotMatchLabel.isVisible())
            passwordDoesNotMatchLabel.setVisible(false);

        if (chosenLanguage == null && !languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(true);
        else if (chosenLanguage != null && languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(false);


        // No label is visible implies that every field is properly filled in
        if (noLabelVisible()) {
            // Then we can create a new user
            // TODO : back-end : user creation implementation
            userSignedUp = true;
            signedUpLabel.setVisible(true);
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

    /**
     * Checks if the name is already taken.
     *
     * @param name - <code>String</code> - the name to check
     * @return <code>boolean</code> - whether the given name is already taken or not
     */
    private boolean isNameTaken(String name) {
        // TODO : back-end : implement this method
        return false;
    }

    /**
     * Checks if the SWIFT is already taken.
     *
     * @param SWIFT - <code>String</code> - the SWIFT to check
     * @return <code>boolean</code> - whether the given SWIFT is already taken or not
     */
    private boolean isSWIFTTaken(String SWIFT) {
        // TODO : back-end : implement this method
        return false;
    }

    @FXML
    public void handleComponentKeyPressed(KeyEvent keyEvent) {
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
    public void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        }
    }
}
