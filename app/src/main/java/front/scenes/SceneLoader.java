package front.scenes;

import app.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Handles the loading of FXML files to create the Scenes.
 * @author Arnaud MOREAU
 */
public class SceneLoader {

    /**
     * Takes the given scene name to find the corresponding FXML file.
     *
     * @param sceneName the name of the scene to load (that is, the name of the FXML file,
     *                  .fxml extension is not mandatory in the string, but it is in the name of the file
     * @param locale    the locale representing the correct language, for example en_US, fr_BE...
     * @return the Scene that was created from the FXML file
     */
    public static Scene load(String sceneName, Locale locale) {
        if (!sceneName.endsWith(".fxml")) sceneName += ".fxml";
        URL url = Main.class.getResource("/xml/scenes/" + sceneName);
        ResourceBundle bundle = ResourceBundle.getBundle("lang.UIData", locale);
        FXMLLoader FXMLLoader = new FXMLLoader(url, bundle);
        try {
            Parent sceneParent = FXMLLoader.load();
            return new Scene(sceneParent, 1280, 720);
        } catch (IOException e) {
            System.out.println("FATAL ERROR while trying to load scene " + url);
            e.printStackTrace();
        }
        return null;
    }
}
