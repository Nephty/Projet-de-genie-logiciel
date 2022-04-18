package front.navigation.navigators;

import javafx.scene.input.MouseEvent;

/**
 * Interface that describes the presence of a "Back" button on the scene. Implemented by scene controllers.
 * @author Arnaud MOREAU
 */
public interface BackButtonNavigator {
    /**
     * Describes the handling of the back button navigation : what should we do (regarding the UI navigation) when the
     * user clicks on the back button ?
     *
     * @param mouseEvent The event received from the action
     */
    void handleBackButtonNavigation(MouseEvent mouseEvent);

    /**
     * Emulates a click on the back button. Used when the user presses escape.
     */
    void emulateBackButtonClicked();
}
