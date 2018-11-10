package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

import static MainPackage.IniTools.initializeINIFile;


/**
 * Main class.
 *
 * @variable sectionGeneral : The general section of the settings.ini.
 * @variable pStage : Main stage of the program.
 *
 * Loads the Main Page with specific conditions.
 */
public class Main extends Application {
    // TODO : out-source bug tests.

    static IniTools sectionGeneral;
    static Stage pStage;

    /**
     * Method runs when program starts.
     *
     * @param primaryStage : The main stage.
     * @throws Exception : Well... yeah :)
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        pStage = primaryStage;
        pStage.initStyle(StageStyle.TRANSPARENT);
        initializeINIFile(new File("Settings.ini"));
        sectionGeneral = new IniTools("GENERAL");
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(root, 1000, 820);
        scene.getStylesheets().add(sectionGeneral.getValue("STYLING_FILE_NAME"));
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
