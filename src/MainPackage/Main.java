package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ini4j.Ini;

import java.io.File;

import static MainPackage.IniTools.*;

public class Main extends Application {

    static IniTools sectionGeneral;

    @Override
    public void start(Stage primaryStage) throws Exception{
        initializeINIFile(new File("Settings.ini"));
        sectionGeneral = new IniTools("GENERAL");
        Parent root = FXMLLoader.load(getClass().getResource("ChooserScreen.fxml"));
        Scene scene = new Scene(root, 1000, 820);
        primaryStage.setTitle(sectionGeneral.getValue("APP_NAME")+ " ver. " + sectionGeneral.getValue("APP_VERSION"));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);

        scene.getRoot().requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
