package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.KeyEvent;

import java.io.File;

import static MainPackage.IniTools.initializeINIFile;


/**
 * Main class.
 *
 * variable sectionGeneral : The general section of the settings.ini.
 * variable pStage : Main stage of the program.
 *
 * Loads the Main Page with specific conditions.
 */
public class Main extends Application {
    // TODO : out-source bugbug tests.

    public static IniTools sectionGeneral;
    public static Stage pStage;
    public static File cssFile;
    public static String cssLoc;

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
        initializeINIFile(new File(System.getProperty("user.dir") + "\\Settings.ini"));
        sectionGeneral = new IniTools("GENERAL");
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(root, 1000, 820);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, MainPageController::handleKeyPressed);
        cssFile = new File(System.getProperty("user.dir") + "\\" + sectionGeneral.getValue("STYLING_FILE_NAME"));
        cssLoc = "file:///" + cssFile.getAbsolutePath().replace("\\","/");
        scene.getStylesheets().add(cssLoc);
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
