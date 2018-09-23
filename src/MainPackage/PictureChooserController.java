package MainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.*;

import static MainPackage.ChooserScreenController.screenController;
import static MainPackage.ChooserScreenController.type;
import static MainPackage.Constants.*;
import static MainPackage.Main.settings;

public class PictureChooserController {

    @FXML
    Button pushMeButton;
    @FXML
    Button exitButton;

    private static FileChooser fc = new FileChooser();
    private static File initialDirectory = new File(System.getProperty("user.dir"));

    private static final File panoramicViewer = new File(settings.get("PICTURE_CHOOSER","PANORAMIC_VIEWER_PATH"));

    static File imageFile;

    public void initialize(){
        fc.setInitialDirectory(initialDirectory);
        pushMeButton.setText(settings.get("PICTURE_CHOOSER","PUSHME_BUTTON_TEXT"));
        exitButton.setText(settings.get("GENERAL","EXIT_BUTTON_TEXT"));
        exitButton.setOnAction(event -> System.exit(0));
    }

    public void handlePushMeButton(){
        if (!RobotFunctions.runFileScan()) {
            imageFile = fc.showOpenDialog(pushMeButton.getScene().getWindow());
            initPanoramicSave(imageFile);
        }
        if (type == TYPE_MULTIPLE) {
            screenController.setScreen(pushMeButton.getScene(),"ThumbnailCreation.fxml");
        }
        else if (type == TYPE_SINGLE){
            screenController.setScreen(pushMeButton.getScene(),"SinglePictureRender.fxml");
        }
    }

    private static void initPanoramicSave(File imageFile){
        try {
            Process process = new ProcessBuilder(panoramicViewer.getPath(), "-openfile", imageFile.getPath()).start();
            RobotFunctions.panViewerSaver();
            RobotFunctions.saveProgressChecker(process);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

}
