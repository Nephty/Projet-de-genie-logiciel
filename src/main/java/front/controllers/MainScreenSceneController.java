package front.controllers;

import front.Main;
import front.navigation.Flow;
import front.navigation.navigators.LanguageButtonNavigator;
import front.scenes.Scenes;
import javafx.scene.input.MouseEvent;

public class MainScreenSceneController implements LanguageButtonNavigator {
    public void handleSignOutButtonClicked(MouseEvent event) {
        Main.setScene(Flow.back());
        // TODO : back-end log out (clear data)
    }

    public void handleLanguageButtonClicked(MouseEvent event) {
        handleLanguageButtonNavigation(event);
    }

    @Override
    public void handleLanguageButtonNavigation(MouseEvent event) {
        Main.setScene(Flow.forward(Scenes.LanguageScene));
    }

    public void handleFinancialProductsButtonClicked(MouseEvent event) {
    }

    public void handleRequestsButtonClicked(MouseEvent event) {
    }

    public void handleNotificationsButtonClicked(MouseEvent event) {
    }

    public void handleChangePasswordButtonClicked(MouseEvent event) {
    }
}
