package front.controllers;

import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.BackButtonNavigator;
import javafx.scene.input.MouseEvent;

public class LanguageSceneController implements BackButtonNavigator {
    // TODO : make distance between label and left border the same as distance between button and right border
    // TODO : change label css style so it doesn't feel like a button
    public void handleBackButtonClicked(MouseEvent mouseEvent) {
        handleBackButtonNavigation(mouseEvent);
    }

    @Override
    public void handleBackButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.back());
    }

    public void handleAddButtonClicked(MouseEvent mouseEvent) {
    }
}
