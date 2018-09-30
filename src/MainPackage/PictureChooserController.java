package MainPackage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;

import static MainPackage.ChooserScreenController.screenController;
import static MainPackage.ChooserScreenController.type;
import static MainPackage.Constants.*;
import static MainPackage.Main.sectionGeneral;

public class PictureChooserController {

    @FXML
    Button pushMeButton;
    @FXML
    Button exitButton;
    @FXML
    Label label;
    @FXML
    TextField textField;
    @FXML
    Button saveButton;
    @FXML
    Button loadProjectButton;
    @FXML
    Button newProjectButton;
    @FXML
    VBox loadBox;
    @FXML
    VBox newBox;
    @FXML
    TitledPane loadPane;
    @FXML
    VBox loadPaneBox;


    private static IniTools sectionPictChoose = new IniTools("PICTURE_CHOOSER");

    private static FileChooser fc = new FileChooser();
    private static File initialDirectory = new File(System.getProperty("user.dir"));
    static File projectFolder;


    final static File projects = new File(System.getProperty("user.dir") + "\\Projects");

    private static File panoramicViewer = new File(sectionPictChoose.getValue("PANORAMIC_VIEWER_PATH"));

    static File imageFile;

    public void initialize(){
        if (!projects.exists()) {
            projects.mkdir();
        }
        loadProjectButton.setText(sectionPictChoose.getValue("LOAD_PROJECT_BUTTON_TEXT"));
        loadProjectButton.setOnAction(event -> {
            loadProjectButton.setDisable(true);
            loadProjectButton.setVisible(false);
            loadBox.setVisible(true);
            loadBox.setDisable(false);
            newProjectButton.setDisable(true);
        });
        newProjectButton.setText(sectionPictChoose.getValue("NEW_PROJECT_BUTTON_TEXT"));
        newProjectButton.setOnAction(event -> {
            newProjectButton.setDisable(true);
            newProjectButton.setVisible(false);
            newBox.setVisible(true);
            newBox.setDisable(false);
            loadProjectButton.setDisable(true);
        });
        loadPane.setText(sectionPictChoose.getValue("LOAD_PROJECT_TEXT"));
        EventHandler<ActionEvent> loadProject = event -> {
            projectFolder = new File(projects + "\\" + ((Button)event.getTarget()).getText());
            if (!RobotFunctions.runFileScan()){
                initPanoramicSave(imageFile);
            }
            screenController.setScreen(pushMeButton.getScene(),"ThumbnailCreation.fxml");
        };
        for (File f: checkProjects()) {
            Button newButton = new Button(f.getName());
            newButton.setOnAction(loadProject);
            loadPaneBox.getChildren().add(newButton);
        }
        fc.setInitialDirectory(initialDirectory);
        pushMeButton.setText(sectionPictChoose.getValue("PUSHME_BUTTON_TEXT"));
        exitButton.setText(sectionGeneral.getValue("EXIT_BUTTON_TEXT"));
        label.setText(sectionPictChoose.getValue("LABEL_TEXT"));
        saveButton.setText(sectionPictChoose.getValue("SAVE_BUTTON_TEXT"));
        saveButton.setOnAction(event -> {
            projectFolder = new File(projects + "\\" + textField.getText());
            projectFolder.mkdir();
            initPanoramicSave(imageFile);
            screenController.setScreen(pushMeButton.getScene(),"ThumbnailCreation.fxml");
        });
        exitButton.setOnAction(event -> System.exit(0));
    }

    public void handlePushMeButton(){
        imageFile = fc.showOpenDialog(pushMeButton.getScene().getWindow());
        textField.setText(imageFile.getName());

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

    private static File[] checkProjects(){
        return projects.listFiles();
    }

}
