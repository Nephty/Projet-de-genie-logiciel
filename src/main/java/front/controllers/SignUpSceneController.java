package front.controllers;

import back.user.User;
import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

public class SignUpSceneController implements BackButtonNavigator, LanguageButtonNavigator {
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
    Label favoriteLanguageLabel;
    @FXML
    ComboBox<String> languageComboBox;
    @FXML
    CheckBox checkBox;

    public void handleBackButtonClicked(MouseEvent event) {
        handleBackButtonNavigation(event);
    }

    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    public void handleSignUpButtonClicked(MouseEvent mouseEvent) {
        String lastName = lastNameField.getText(), firstName = firstNameField.getText(), email = emailAddressField.getText(),
                NRN = NRNField.getText(), username = usernameField.getText(), password = passwordField.getText(),
                passwordConfirmation = confirmPasswordField.getText(), chosenLanguage = languageComboBox.getValue();

        // TODO : back-end replace "EN_US", "FR_BE" by the available languages in the system
        ObservableList<String> values = FXCollections.observableArrayList(Arrays.asList("EN_US", "FR_BE"));
        languageComboBox.setItems(values);

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
        if (!passwordMatches(password, passwordConfirmation) && !passwordDoesNotMatchLabel.isVisible()) passwordDoesNotMatchLabel.setVisible(true);
        else if (passwordMatches(password, passwordConfirmation) && passwordDoesNotMatchLabel.isVisible()) passwordDoesNotMatchLabel.setVisible(false);

        if (chosenLanguage == null && !languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(true);
        else if (chosenLanguage != null && languageNotChosenLabel.isVisible()) languageNotChosenLabel.setVisible(false);


        // No label is visible implies that every field is properly filled in
        if (noLabelVisible()) {
            // Then we can create a new user
            Main.setUser(User.getInstance(lastName, firstName, NRN, email, username));
        }

        System.out.println(Main.getUser());

        // TODO : back-end signing up process implementation
    }

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
        return (!lastName.equals("") && (lastName != null) && (lastName.matches("^[a-zA-Z-]*$")));
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
        if (email.equals("") || email == null || !email.matches("^[a-zA-Z0-9@.]*$")) return false;
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
        if (NRN.equals("") || NRN == null || NRN.length() != 15) return false;
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
        if (username.equals("") || username == null || username.length() > 32) return false;
        return username.matches("^[a-zA-Z0-9]*$");
    }

    /**
     * Checks if the username is already taken.
     * @param username - <code>String</code> - the username to check
     * @return <code>boolean</code> - whether the given username is already take or not
     */
    private boolean isUsernameTaken(String username) {
        // TODO : back-end implementation
        return false;
    }

    /**
     * Checks if the email is already taken.
     * @param email - <code>String</code> - the email to check
     * @return <code>boolean</code> - whether the given email is already take or not
     */
    private boolean isEmailTaken(String email) {
        // TODO : back-end implementation
        return false;
    }

    /**
     * Checks if the NRN is already taken.
     * @param NRN - <code>String</code> - the NRN to check
     * @return <code>boolean</code> - whether the given NRN is already take or not
     */
    private boolean isNRNTaken(String NRN) {
        // TODO : back-end implementation
        return false;
    }

    /**
     * Checks if the given passwords match and are not empty.
     * @param password - <code>String</code> - the password
     * @param passwordConfirmation - <code>String</code> - the password confirmation
     * @return <code>boolean</code> - whether the two passwords match or not
     */
    private boolean passwordMatches(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation) && !password.equals("");
    }
}
