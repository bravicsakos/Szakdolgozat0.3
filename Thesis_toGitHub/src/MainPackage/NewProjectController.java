package MainPackage;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static MainPackage.Main.sectionGeneral;
import static MainPackage.MainPageController.*;

/**
 * Class for crating new project window
 *
 * @variable mainPane : Main pane from fxml file.
 * @variable chooseButton : Button for choosing image file.
 * @variable label : Label for info on text field.
 * @variable textField : Name of the project to be created.
 * @variable saveButton : Button for saving the new project.
 * @variable cancelButton : Exits the window.
 * @variable savecancelBox : Box containing save and cancel buttons.
 * @variable contentBox : box containing all the items listed before.
 *
 * @variable sectionNewProject : New project section of the settings.ini file.
 *
 * @variable fc : File chooser for the choose button.
 * @variable initialDirectory : Initial directory for the file chooser.
 *
 * @variable projects : The folder containing all the projects.
 * @variable panoramicViewer : The Panoramic Viewer executable file.
 *
 * @variable imageFile : Main image for the project.
 */
public class NewProjectController {

    @FXML
    StackPane mainPane;

    private static Button chooseButton = new Button();
    private static Label label = new Label();
    private static TextField textField = new TextField();
    private static Button saveButton = new Button();
    private static Button cancelButton = new Button();
    private static HBox savecancelBox = new HBox();
    private static VBox contentBox = new VBox();

    private static IniTools sectionNewProject = new IniTools("NEW_PROJECT");

    private static FileChooser fc = new FileChooser();
    private static File initialDirectory = new File(System.getProperty("user.dir"));


    private final static File projects = new File(System.getProperty("user.dir") + "\\Projects");

    private static File panoramicViewer = new File(sectionGeneral.getValue("PANORAMIC_VIEWER_PATH"));

    private static File imageFile;

    public void initialize(){
        mainPane.getStyleClass().add("Custom-BorderOnly");
        reset();
        if (!projects.exists()) {
            boolean ignored = projects.mkdir(); // TODO : don't ignore it ;)
        }

        Text errorText = new Text();
        errorText.getStyleClass().add("Custom-Error-Text");

        chooseButton.setText(sectionNewProject.getValue("CHOOSE_BUTTON_TEXT"));
        chooseButton.getStyleClass().add("Custom-Button1");
        chooseButton.setOnAction(actionEvent -> {
            imageFile = fc.showOpenDialog(chooseButton.getScene().getWindow());
            File dataFolder = new File(imageFile.getParentFile().getPath() + "\\" + imageFile.getName().substring(0,imageFile.getName().length() - 5));
            if (!imageFile.getName().matches(".+\\.mrxs")){
                errorText.setText("File doesn't match the criteria:\n Type must be '.mrxs'");
                saveButton.setDisable(true);
                dialogNew.setHeight(210);
                textField.setText("");
            }
            else if (!dataFolder.exists() || (dataFolder.list() == null && Objects.requireNonNull(dataFolder.list()).length == 0)){
                errorText.setText("File doesn't have data folder next to it\n or data folder is empty");
                saveButton.setDisable(true);
                dialogNew.setHeight(210);
                textField.setText("");
            }
            else {
                saveButton.setDisable(false);
                errorText.setText("");
                dialogNew.sizeToScene();
                textField.setText(imageFile.getName());
            }
        });

        label.setText(sectionNewProject.getValue("LABEL_TEXT"));
        label.getStyleClass().add("Custom-Text");

        textField.setMaxWidth(200);
        textField.getStyleClass().add("Custom-Button2");

        saveButton.setText(sectionNewProject.getValue("SAVE_BUTTON_TEXT"));
        saveButton.getStyleClass().add("Custom-Button1");
        saveButton.setOnAction(actionEvent -> {
            if (textField.getText().trim().equals("")){
                errorText.setText("Please set a name\n for the project");
                dialogNew.setHeight(210);
            }
            else if (LoadProjectController.checkProjects() == null){
                boolean ignored; // TODO : don't ignore.
            }
            else {
                boolean doesExist = false;
                for (File f : LoadProjectController.checkProjects()) {
                    if (f.getName().equals(textField.getText())) {
                        errorText.setText("Project with that name\nalready exists!");
                        dialogNew.setHeight(210);
                        doesExist = true;
                        break;
                    }
                }
                if (!doesExist) {
                    dialogNew.sizeToScene();
                    errorText.setText("");
                    main.setCenter(null);
                    main.setRight(null);
                    main.setLeft(null);
                    MainPageController.constructProjectText(textField.getText());
                    projectFolder = new File(projects + "\\" + textField.getText());
                    initPanoramicSave(imageFile);
                    ThumbnailCreationController.run();
                    dialogNew.close();
                }
            }
        });

        cancelButton.setText(sectionGeneral.getValue("CANCEL_BUTTON_TEXT"));
        cancelButton.getStyleClass().add("Custom-Button1");
        cancelButton.setOnAction(actionEvent -> dialogNew.close());

        savecancelBox.getChildren().add(saveButton);
        savecancelBox.getChildren().add(cancelButton);
        savecancelBox.setAlignment(Pos.TOP_CENTER);

        contentBox.getChildren().addAll(chooseButton,label,textField,savecancelBox,errorText);
        contentBox.setAlignment(Pos.TOP_CENTER);

        BorderPane main = new BorderPane();
        main.getStyleClass().add("Custom-BackGround");

        main.setCenter(contentBox);
        mainPane.getChildren().add(main);
        fc.setInitialDirectory(initialDirectory);
    }

    /**
     * Starts the Panoramic Viewer to cut the given image.
     *
     * @param imageFile : The image to be cut.
     *
     * Starts the program as a process and gives it the image
     * as argument then starts the {@Class RobotFuntions} methods to
     * make the image save.
     */
    private static void initPanoramicSave(File imageFile){
        // TODO : What if the program doesn't run for some reason
        try {
            Process process = new ProcessBuilder(panoramicViewer.getPath(), "-openfile", imageFile.getPath()).start();
            RobotFunctions.panViewerSaver();
            RobotFunctions.saveProgressChecker(process);

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Resets the window so that nothing is left
     * from previous runs.
     */
    private void reset(){
        if (mainPane.getChildren().size() >= 1) {
            mainPane.getChildren().subList(1, mainPane.getChildren().size() + 1).clear();
        }
        chooseButton = new Button();
        label = new Label();
        textField = new TextField();
        saveButton = new Button();
        cancelButton = new Button();
        savecancelBox = new HBox();
        contentBox = new VBox();
    }
}
