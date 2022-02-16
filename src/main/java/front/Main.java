package front;

import front.XML.XMLElement;
import front.XML.XMLResolver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Enumeration;
import java.util.ResourceBundle;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        XMLResolver XMLColorResolver = new XMLResolver("values/colors.xml");
        XMLResolver XMLDimensionResolver = new XMLResolver("values/dimensions.xml");
        XMLResolver XMLStringResolver = new XMLResolver("values/strings.xml");

        for (XMLElement c : XMLColorResolver.content) {
            System.out.println(new Color(Integer.parseInt(c.value, 16)));
        }

        for (XMLElement d : XMLDimensionResolver.content) {
            System.out.println(d);
        }

        for (XMLElement s : XMLStringResolver.content) {
            System.out.println(s);
        }

        Parent root = FXMLLoader.load(getClass().getResource("/xml/scenes/AuthScene.fxml"));

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
}
