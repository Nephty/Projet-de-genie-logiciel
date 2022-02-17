package front;

import front.XML.XMLElement;
import front.XML.XMLResolver;
import front.navigation.Flow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;


public class Main extends Application {
    private static Stage stage;
    public static Scene AuthScene, SignInScene, LanguageScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage_) throws Exception {
        stage = stage_;

        URL AuthSceneFXMLLocation = getClass().getResource("/xml/scenes/AuthScene.fxml");
        FXMLLoader AuthSceneFXMLLoader = new FXMLLoader(AuthSceneFXMLLocation);
        Parent AuthSceneParent = AuthSceneFXMLLoader.load();
        AuthScene = new Scene(AuthSceneParent, 1280, 720);

        URL SignInSceneFXMLLocation = getClass().getResource("/xml/scenes/SignInScene.fxml");
        FXMLLoader SignInSceneFXMLLoader = new FXMLLoader(SignInSceneFXMLLocation);
        Parent SignInSceneParent = SignInSceneFXMLLoader.load();
        SignInScene = new Scene(SignInSceneParent, 1280, 720);

        URL LanguageSceneFXMLLocation = getClass().getResource("/xml/scenes/LanguageScene.fxml");
        FXMLLoader LanguageSceneFXMLLoader = new FXMLLoader(LanguageSceneFXMLLocation);
        Parent LanguageSceneParent = LanguageSceneFXMLLoader.load();
        LanguageScene = new Scene(LanguageSceneParent, 1280, 720);

        Flow.add(AuthScene);
        stage.setResizable(false);
        stage.setTitle("FXML Welcome");
        stage.setScene(AuthScene);
        stage.show();
    }

    public static void setScene(Scene scene) {
        stage.setScene(scene);
    }
}
