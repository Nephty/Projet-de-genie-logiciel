package front.navigation.navigators;

import javafx.scene.input.MouseEvent;

public abstract class AbsBackButtonNavigator {
    public void handleBackButtonNavigation(MouseEvent event) {
        System.out.println("You clicked the back button");
    }
}
