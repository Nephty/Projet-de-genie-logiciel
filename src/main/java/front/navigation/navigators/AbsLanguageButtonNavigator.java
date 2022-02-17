package front.navigation.navigators;

import javafx.scene.input.MouseEvent;

public abstract class AbsLanguageButtonNavigator {
    public void handleLanguageButtonNavigation(MouseEvent event) {
        System.out.println("You clicked the language button");
    }
}
