package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ini4j.Ini;

import java.io.File;

public class Main extends Application {

    static Ini settings = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        settings = new Ini(new File("Settings.ini"));
        Parent root = FXMLLoader.load(getClass().getResource("ChooserScreen.fxml"));
        Scene scene = new Scene(root, 1000, 820);
        primaryStage.setTitle(settings.get("GENERAL","APP_NAME") + " ver. " + settings.get("GENERAL","APP_VERSION"));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);

        scene.getRoot().requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
