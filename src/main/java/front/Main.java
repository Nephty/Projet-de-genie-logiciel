package front;

import front.XML.XMLColor;
import front.XML.XMLDimension;
import front.XML.XMLResolver;
import front.XML.XMLString;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        XMLResolver<XMLColor> XMLColorResolver = new XMLResolver<>("values/colors.xml");
        //XMLResolver<XMLDimension> XMLDimensionResolver = new XMLResolver<>("values/dimensions.xml");
        //XMLResolver<XMLString> XMLStringResolver = new XMLResolver<>("values/strings.xml");

        ObjectMapper m = new XmlMapper();
        InputStream is = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/xml/" + "values/colors.xml");
        TypeReference<List<XMLColor>> tr = new TypeReference<>() {};
        List<XMLColor> content1 = m.readValue(is, tr);
        for (XMLColor hm : content1) {
            System.out.println(hm);
        }

        /*
        for (XMLColor c : XMLColorResolver.content) { // TODO : why not working ?
                                                      // hints : in main, typeReference is of type List<XMLColor>,
                                                      // whereas in XMLResolver it is of type List<T>,
                                                      // but then why is content an ArrayList<LinkedHashMap> ?
                                                      // maybe it is by default

            System.out.println(c);
        }

        for (XMLDimension d : XMLDimensionResolver.content) {
            System.out.println(d);
        }

        for (XMLString s : XMLStringResolver.content) {
            System.out.println(s);
        }
         */

        Parent root = FXMLLoader.load(getClass().getResource("/xml/scenes/AuthScene.fxml"));

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
}
