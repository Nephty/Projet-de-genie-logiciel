package client.front.navigation.navigators;

import javafx.scene.input.MouseEvent;

/**
 * Interface that describes the presence of a "Language" button on the scene. Implemented by scene controllers.
 */
public interface LanguageButtonNavigator {
    void handleLanguageButtonNavigation(MouseEvent event);
}
