package front.controllers;

import app.Main;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

/**
 * @author Arnaud MOREAU
 */
public class SignUpSceneController extends Controller implements BackButtonNavigator {
    @FXML
    TextField confirmPasswordTextField, passwordTextField;
    @FXML
    CheckBox showHidePasswordCheckBox;
    @FXML
    Label NRNTakenLabel, emailTakenLabel, passwordDoesNotMatchLabel, languageNotChosenLabel,
            invalidLastNameLabel, invalidFirstNameLabel, invalidEmailLabel, invalidNRNLabel, usernameLabel;
    @FXML
    Button backButton, signUpButton;
    @FXML
    TextField lastNameField, firstNameField, NRNField, emailAddressField;
    @FXML
    PasswordField passwordField, confirmPasswordField;
    @FXML
    Label favoriteLanguageLabel, signedUpLabel;
    @FXML
    ComboBox<String> languageComboBox;

    private boolean userSignedUp = false;

    public void initialize() {
        ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList(Main.FR_BE_Locale.getDisplayName(), Main.EN_US_Locale.getDisplayName(), Main.NL_NL_Locale.getDisplayName(), Main.PT_PT_Locale.getDisplayName(), Main.LT_LT_Locale.getDisplayName(), Main.RU_RU_Locale.getDisplayName(), Main.DE_DE_Locale.getDisplayName(), Main.PL_PL_Locale.getDisplayName()));
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

        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> usernameLabel.setText("   Username : " + getUsernameFromLastNameAndNRNTextFields()));
        NRNField.textProperty().addListener((observable, oldValue, newValue) -> usernameLabel.setText("   Username : " + getUsernameFromLastNameAndNRNTextFields()));
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

    @FXML
    void handleSignUpButtonClicked(MouseEvent mouseEvent) {
        signUp();
    }

    /**
     * Checks if every field is properly filled in. Initializes the sign-up process.
     */
    public void signUp() {
        String lastName = lastNameField.getText(), firstName = firstNameField.getText(), email = emailAddressField.getText(),
                NRN = NRNField.getText(), password = passwordField.getText(),
                passwordConfirmation = confirmPasswordField.getText(), chosenLanguage = languageComboBox.getValue();

        // Manage the "invalid xxxx" labels visibility
        // Is the last name valid (A-Z, a-z) ? Show/hide the incorrect last name label accordingly
        if (!isValidLastName(lastName) && !invalidLastNameLabel.isVisible()) invalidLastNameLabel.setVisible(true);
        else if (isValidLastName(lastName) && invalidLastNameLabel.isVisible()) invalidLastNameLabel.setVisible(false);
        // Is the first name valid (A-Z, a-z) ? Show/hide the incorrect first name label accordingly
        if (!isValidFirstName(firstName) && !invalidFirstNameLabel.isVisible()) invalidFirstNameLabel.setVisible(true);
        else if (isValidFirstName(firstName) && invalidFirstNameLabel.isVisible())
            invalidFirstNameLabel.setVisible(false);
        // Is the email valid (A-Z, a-z, 0-9, contains only one @, contains a . after the @) ? Show/hide the incorrect email label accordingly
        if (!isValidEmail(email) && !invalidEmailLabel.isVisible()) invalidEmailLabel.setVisible(true);
        else if (isValidEmail(email) && invalidEmailLabel.isVisible()) invalidEmailLabel.setVisible(false);
        // Is the NRN valid (XX.XX.XX-XXX.XX format with numbers)
        if (!isValidNRN(NRN) && !invalidNRNLabel.isVisible()) invalidNRNLabel.setVisible(true);
        else if (isValidNRN(NRN) && invalidNRNLabel.isVisible()) invalidNRNLabel.setVisible(false);


        // PRO TIP : if the username is invalid, it cannot be taken, so we can safely give the same layout (coordinates)
        // the labels "invalid username" and "username taken". If the label "invalid username" shows up, it is
        // impossible for the "username taken" label to show up and vice versa.


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

            String birthDate;
            if(Integer.parseInt(NRN.substring(0,2)) >=30){
                birthDate = "19" + NRN.substring(0,2) + "-" + NRN.substring(3,5) + "-" +NRN.substring(6,8);
            } else{
                birthDate = "20" + NRN.substring(0,2) + "-" + NRN.substring(3,5) + "-" +NRN.substring(6,8);
            }

            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = null;
            String username = getUsernameFromLastNameAndNRNTextFields();
            try {
                response = Unirest.post("https://flns-spring-test.herokuapp.com/api/user")
                        .header("Content-Type", "application/json")
                        .body("{\r\n    \"username\": \"" + username + "\",\r\n    \"userId\": \"" + NRN + "\",\r\n    \"email\": \"" + email + "\",\r\n    \"password\": \"" + password + "\",\r\n    \"firstname\": \"" + firstName + "\",\r\n    \"lastname\": \"" + lastName + "\",\r\n    \"language\": \"" + chosenLanguage + "\",\r\n    \"birthdate\": \""+birthDate+"\"\r\n}")                        .asString();
                Main.errorCheck(response.getStatus());
            } catch (UnirestException e) {
                Main.ErrorManager(408);
            }
            if (response != null) {
                if (response.getStatus() == 403) {
                    switch (response.getBody()) {
                        case "ID":
                            NRNTakenLabel.setVisible(true);
                            break;
                        case "EMAIL":
                            emailTakenLabel.setVisible(true);
                            break;
                    }
                } else {
                    fadeInAndOutNode(3000, signedUpLabel);
                    userSignedUp = true;
                }
            }
        }
    }

    /**
     * Checks if the given <code>String</code> is a valid last name.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z and from A-Z or a dash (-)
     *
     * @param lastName - <code>String</code> - the last name to check
     * @return <code>boolean</code> - whether the given last name is a valid last name or not
     */
    public static boolean isValidLastName(String lastName) {
        if (lastName == null) return false;
        return (!lastName.equals("") && (lastName.matches("^[a-zA-Z- ]*$")));
    }

    /**
     * Checks if the given <code>String</code> is a valid first name.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z, from A-Z or a dash (-).
     *
     * @param firstName - <code>String</code> - the last name to check
     * @return <code>boolean</code> - whether the given first name is a valid first name or not
     */
    public static boolean isValidFirstName(String firstName) {
        return isValidLastName(firstName);
    }

    /**
     * Checks if the given <code>String</code> is a valid email.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must only contain characters from a-z, from A-Z, from 0-9 or characters that are either @ or .
     * - string must only contain one @
     * - string must contain at most one . after the @
     * - string must contain at least one character from a-z or from A-Z before the @
     *
     * @param email - <code>String</code> - the email to check
     * @return <code>boolean</code> - whether the given email is a valid email or not
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        if (email.equals("") || !email.matches("^[a-zA-Z0-9@.]*$")) return false;
        boolean hasOneAt = false;  // if the string contains at least one @
        boolean hasOneDotAfterAt = false;  // if the string contains at least one . after the first @
        boolean hasOneCharBeforeAt = false; // if the string contains at least one character before the first @
        for (char c : email.toCharArray()) {
            if (c == '@' && hasOneAt) return false; // string has 2 @, invalid email
            if (c == '@') hasOneAt = true; // first @ we encounter
            if (c == '.' && hasOneAt) hasOneDotAfterAt = true; // first . we encounter after the first @
            if (("" + c).matches("^[a-zA-Z]") && !hasOneAt) hasOneCharBeforeAt = true;
        }
        return hasOneAt && hasOneDotAfterAt && hasOneCharBeforeAt;
    }

    /**
     * Checks if the given string is a valid NRN.
     * Requirements :
     * - string must not be empty
     * - string must not be null
     * - string must match the format XX.XX.XX-XXX.XX where X in an integer in range 0-9
     *
     * @param NRN - <code>String</code> - the NRN to check
     * @return <code>boolean</code> whether the given NRN is a valid NRN or not
     */
    public static boolean isValidNRN(String NRN) {
        if (NRN == null) return false;
        if (NRN.length() != 15) return false;  // NRN.length() == 15 already checks NRN != ""
        for (int i = 0; i < 15; i++) {
            switch (i) {
                case 0:
                case 1:
                case 3:
                case 4:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                    if (!Character.isDigit(NRN.charAt(i))) return false;
                    break;
                case 2:
                case 5:
                case 12:
                    if (NRN.charAt(i) != '.') return false;
                    break;
                case 8:
                    if (NRN.charAt(i) != '-') return false;
                    break;
            }
        }
        return true;
    }

    /**
     * Checks if any label that show if any field is not properly filled in is visible. If any is visible,
     * the user didn't properly fill in every field. If none are visible, every field is properly filled in.
     * This is directly used to check if every field is properly filled in to begin the sign-up process.
     *
     * @return <code>boolean</code> - whether any label is visible or not
     */
    private boolean noLabelVisible() {
        return !invalidLastNameLabel.isVisible() && !invalidFirstNameLabel.isVisible() && !invalidEmailLabel.isVisible()
                && !invalidNRNLabel.isVisible()
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
        NRNTakenLabel.setVisible(false);
        emailTakenLabel.setVisible(false);
        passwordDoesNotMatchLabel.setVisible(false);
        languageNotChosenLabel.setVisible(false);
        invalidLastNameLabel.setVisible(false);
        invalidFirstNameLabel.setVisible(false);
        invalidEmailLabel.setVisible(false);
        invalidNRNLabel.setVisible(false);
    }

    /**
     * Removes all text entered in all text fields.
     */
    public void emptyAllTextFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        NRNField.setText("");
        emailAddressField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    @FXML
    void handleComponentKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            emulateBackButtonClicked();
            event.consume();
        } else if (event.getCode() == KeyCode.ENTER) {
            emulateSignUpButtonClicked();
            event.consume();
        }
    }

    String getUsernameFromLastNameAndNRNTextFields() {
        return lastNameField.getText().replace("-", "").replace(" ", "") + NRNField.getText().replace("-", "").replace(".", "");
    }
}
