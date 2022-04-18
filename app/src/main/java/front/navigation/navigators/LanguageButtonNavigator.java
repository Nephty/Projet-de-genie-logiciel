package front.navigation.navigators;

import javafx.scene.input.MouseEvent;

/**
 * Interface that describes the presence of a "Language" button on the scene. Implemented by scene controllers.
 * @author Arnaud MOREAU
 */
public interface LanguageButtonNavigator {
    /**
     * Describes the handling of the language button navigation : what should we do (regarding the UI navigation) when the
     * user clicks on the language button ?
     *
     * @param mouseEvent The event received from the action
     */
    void handleLanguageButtonNavigation(MouseEvent mouseEvent);
}
