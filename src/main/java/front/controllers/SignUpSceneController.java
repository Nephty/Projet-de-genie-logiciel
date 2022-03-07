package front.controllers;

import BenkyngApp.Main;
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
    Label NRNTakenLabel, emailTakenLabel, usernameTakenLabel, passwordDoesNotMatchLabel, languageNotChosenLabel,
        invalidLastNameLabel, invalidFirstNameLabel, invalidEmailLabel, invalidNRNLabel, invalidUsernameLabel;
    @FXML
    Button backButton, languageButton, signUpButton;
    @FXML
    TextField lastNameField, firstNameField, NRNField, emailAddressField, usernameField;
    @FXML
    PasswordField passwordField, confirmPasswordField;
    @FXML
    Label favoriteLanguageLabel, signedUpLabel;
    @FXML
    ComboBox<String> languageComboBox;
    @FXML
    CheckBox checkBox;

    private boolean userSignedUp = false;

    public void initialize() {
        ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("EN_US", "FR_BE"));
        // TODO : back-end : fetch all available languages and put them in the list
        languageComboBox.setItems(values);
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
        String lastName = lastNameField.getText(), firstName = firstNameField.getText(), email = emailAddressField.getText(),
                NRN = NRNField.getText(), username = usernameField.getText(), password = passwordField.getText(),
                passwordConfirmation = confirmPasswordField.getText(), chosenLanguage = languageComboBox.getValue();

        // Manage the "invalid xxxx" labels visibility
        // Is the last name valid (A-Z, a-z) ? Show/hide the incorrect last name label accordingly
        if (!isValidLastName(lastName) && !invalidLastNameLabel.isVisible()) invalidLastNameLabel.setVisible(true);
        else if (isValidLastName(lastName) && invalidLastNameLabel.isVisible()) invalidLastNameLabel.setVisible(false);
        // Is the first name valid (A-Z, a-z) ? Show/hide the incorrect first name label accordingly
        if (!isValidFirstName(firstName) && !invalidFirstNameLabel.isVisible()) invalidFirstNameLabel.setVisible(true);
        else if (isValidFirstName(firstName) && invalidFirstNameLabel.isVisible()) invalidFirstNameLabel.setVisible(false);
        // Is the email valid (A-Z, a-z, 0-9, contains only one @, contains a . after the @) ? Show/hide the incorrect email label accordingly
        if (!isValidEmail(email) && !invalidEmailLabel.isVisible()) invalidEmailLabel.setVisible(true);
        else if (isValidEmail(email) && invalidEmailLabel.isVisible()) invalidEmailLabel.setVisible(false);
        // Is the NRN valid (XX.XX.XX-XXX.XX format with numbers)
        if (!isValidNRN(NRN) && !invalidNRNLabel.isVisible()) invalidNRNLabel.setVisible(true);
        else if (isValidNRN(NRN) && invalidNRNLabel.isVisible()) invalidNRNLabel.setVisible(false);
        // Is the username valid (numbers and letters only)
        if (!isValidUsername(username) && !invalidUsernameLabel.isVisible()) invalidUsernameLabel.setVisible(true);
        else if (isValidUsername(username) && invalidUsernameLabel.isVisible()) invalidUsernameLabel.setVisible(false);


        // PRO TIP : if the username is invalid, it cannot be taken, so we can safely give the same layout (coordinates)
        // the labels "invalid username" and "username taken". If the label "invalid username" shows up, it is
        // impossible for the "username taken" label to show up and vice versa.


        // Manage the "xxxx already taken" labels visibility
        // Is the username already taken ?
        if (isUsernameTaken(username) && !usernameTakenLabel.isVisible()) usernameTakenLabel.setVisible(true);
        else if (!isUsernameTaken(username) && usernameTakenLabel.isVisible()) usernameTakenLabel.setVisible(false);
        // Is the email already taken ?
        if (isEmailTaken(email) && !emailTakenLabel.isVisible()) emailTakenLabel.setVisible(true);
        else if (!isEmailTaken(email) && emailTakenLabel.isVisible()) emailTakenLabel.setVisible(false);
        // Is the NRN already taken ?
        if (isNRNTaken(NRN) && !NRNTakenLabel.isVisible()) NRNTakenLabel.setVisible(true);
        else if (!isNRNTaken(NRN) && NRNTakenLabel.isVisible()) NRNTakenLabel.setVisible(false);

        // Manage the "password does not match" label visibility
        if (!passwordMatchesAndIsNotEmpty(password, passwordConfirmation) && !passwordDoesNotMatchLabel.isVisible()) passwordDoesNotMatchLabel.setVisible(true);
        else if (passwordMatchesAndIsNotEmpty(password, passwordConfirmation) && passwordDoesNotMatchLabel.isVisible()) passwordDoesNotMatchLabel.setVisible(false);

        if (chosenLanguage == null && !languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(true);
        else if (chosenLanguage != null && languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(false);


        // No label is visible implies that every field is properly filled in
        if (noLabelVisible()) {
            // Then we can create a new user
            // TODO : back-end : user creation implementation
            userSignedUp = true;
            signedUpLabel.setVisible(true);
            // Empty all data that we don't need, it's a security detail
            lastName = ""; firstName = ""; email = ""; NRN = ""; username = ""; password = "";
            passwordConfirmation = ""; chosenLanguage = "";

        }
    }

    /**
     * Checks if any label that show if any field is not properly filled in is visible. If any is visible,
     * the user didn't properly fill in every field. If none are visible, every field is properly filled in.
     * This is directly used to check if every field is properly filled in to begin the sign up process.
     * @return <code>boolean</code> - whether any label is visible or not
     */
    private boolean noLabelVisible() {
        return !invalidLastNameLabel.isVisible() && !invalidFirstNameLabel.isVisible() && !invalidEmailLabel.isVisible()
                && !invalidNRNLabel.isVisible() && !invalidUsernameLabel.isVisible()
                && !passwordDoesNotMatchLabel.isVisible() && !languageNotChosenLabel.isVisible();
    }

    /**
     * Checks if the given <code>String</code> is a valid last name.
     * Requirements :
     *  - string must not be empty
     *  - string must not be null
     *  - string must only contain characters from a-z and from A-Z
     * @param lastName - <code>String</code> - the last name to check
     * @return <code>boolean</code> - whether the given last name is a valid last name or not
     */
    public static boolean isValidLastName(String lastName) {
        if (lastName == null) return false;
        return (!lastName.equals("") && (lastName.matches("^[a-zA-Z-]*$")));
    }

    /**
     * Checks if the given <code>String</code> is a valid first name.
     * Requirements :
     *  - string must not be empty
     *  - string must not be null
     *  - string must only contain characters from a-z, from A-Z or a dash (-).
     * @param firstName - <code>String</code> - the last name to check
     * @return <code>boolean</code> - whether the given first name is a valid first name or not
     */
    public static boolean isValidFirstName(String firstName) {
        return isValidLastName(firstName);
    }

    /**
     * Checks if the given <code>String</code> is a valid email.
     * Requirements :
     *  - string must not be empty
     *  - string must not be null
     *  - string must only contain characters from a-z, from A-Z, from 0-9 or characters that are either @ or .
     *  - string must only contain one @
     *  - string must contain at most one . after the @
     *  - string must contain at least one character from a-z or from A-Z before the @
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
     *  - string must not be empty
     *  - string must not be null
     *  - string must match the format XX.XX.XX-XXX.XX where X in an integer in range 0-9
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
     * Checks if the given string is valid username.
     * Requirements :
     *  - username must not be empty
     *  - username must not be null
     *  - username must not be longer than 32 characters
     *  - username must contain characters from a-z, from A-Z and from 0-9
     * @param username - <code>String</code> - the username to check
     * @return whether the given username is a valid username or not
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        if (username.equals("") || username.length() > 32) return false;
        return username.matches("^[a-zA-Z0-9]*$");
    }

    /**
     * Checks if the username is already taken.
     * @param username - <code>String</code> - the username to check
     * @return <code>boolean</code> - whether the given username is already take or not
     */
    private boolean isUsernameTaken(String username) {
        // TODO : back-end : implement this method
        return false;
    }

    /**
     * Checks if the email is already taken.
     * @param email - <code>String</code> - the email to check
     * @return <code>boolean</code> - whether the given email is already take or not
     */
    private boolean isEmailTaken(String email) {
        // TODO : back-end : implement this method
        return false;
    }

    /**
     * Checks if the NRN is already taken.
     * @param NRN - <code>String</code> - the NRN to check
     * @return <code>boolean</code> - whether the given NRN is already take or not
     */
    private boolean isNRNTaken(String NRN) {
        // TODO : back-end : implement this method
        return false;
    }

    @FXML
    public void handleFirstNameFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handleLastNameFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handleNRNFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handleEmailFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handleUsernameFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handlePasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handleConfirmPasswordFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) emulateSignUpButtonClicked();
    }

    @FXML
    public void handleLanguageComboBoxKeyPressed(KeyEvent keyEvent) {
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
        usernameTakenLabel.setVisible(false);
        passwordDoesNotMatchLabel.setVisible(false);
        languageNotChosenLabel.setVisible(false);
        invalidLastNameLabel.setVisible(false);
        invalidFirstNameLabel.setVisible(false);
        invalidEmailLabel.setVisible(false);
        invalidNRNLabel.setVisible(false);
        invalidUsernameLabel.setVisible(false);
    }

    /**
     * Removes all text entered in all text fields.
     */
    public void emptyAllTextFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        NRNField.setText("");
        emailAddressField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}
