package front.navigation.navigators;

import javafx.scene.input.MouseEvent;

/**
 * Interface that describes the presence of a "Back" button on the scene. Implemented by scene controllers.
 */
public interface BackButtonNavigator {
    void handleBackButtonNavigation(MouseEvent event);
    void emulateBackButtonClicked();
}
