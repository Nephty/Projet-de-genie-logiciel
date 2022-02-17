package front;

import front.XML.XMLElement;
import front.XML.XMLResolver;
import front.navigation.Flow;
import front.scenes.SceneLoader;
import front.scenes.Scenes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;


public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage_) throws Exception {
        stage = stage_;

        Scenes.AuthScene = SceneLoader.load("AuthScene.fxml", getClass());
        Scenes.SignInScene = SceneLoader.load("SignInScene.fxml", getClass());
        Scenes.LanguageScene = SceneLoader.load("LanguageScene.fxml", getClass());
        Scenes.SignUpScene = SceneLoader.load("SignUpScene.fxml", getClass());

        Flow.add(Scenes.AuthScene);
        stage.setResizable(false);
        stage.setTitle("FXML Welcome");
        stage.setScene(Scenes.AuthScene);
        stage.show();
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }
}
