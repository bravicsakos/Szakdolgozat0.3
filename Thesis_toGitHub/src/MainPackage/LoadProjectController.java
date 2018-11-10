package MainPackage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;

import static MainPackage.Main.sectionGeneral;
import static MainPackage.MainPageController.dialogLoad;
import static MainPackage.MainPageController.projectFolder;


/**
 * Class for making the load projects window.
 *
 * @variable mainPane : Main pane inside FXML file ( InstallViewer.fxml ).
 * @variable label : Label for the project chooser
 * @variable sectionLoadProject : Load project section inside settings.ini.
 * @variable projects : Folder where the projects can be found
 * @variable infoBox : Error information container
 */
public class LoadProjectController {

    @FXML
    AnchorPane mainPane;

    private static Label label = new Label();

    private static IniTools sectionLoadProject = new IniTools("LOAD_PROJECT");

    private final static File projects = new File(System.getProperty("user.dir") + "\\Projects");

    private static HBox infoBox;


    /**
     * Creating the window and adding functionality.
     */
    public void initialize(){
        mainPane.getStyleClass().add("Custom-BorderOnly");
        mainPane.setPadding(new Insets(10));
        if (projects.exists()){
            label.setText(sectionLoadProject.getValue("LABEL_TEXT"));
            label.getStyleClass().add("Custom-Text");

            VBox box = new VBox();
            box.setSpacing(7);
            EventHandler<ActionEvent> load = event -> {
                projectFolder = new File(projects + "\\" + ((Button)event.getTarget()).getText());
                if (!RobotFunctions.runFileScan()){
                    showErrorText(true);
                }
                else {
                    MainPageController.constructProjectText(((Button)event.getTarget()).getText());
                    showErrorText(false);
                    ThumbnailCreationController.run();
                    dialogLoad.close();
                }
            };
            for (File f : checkProjects()){
                Button btn = new Button();
                btn.setText(f.getName());
                btn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
                btn.getStyleClass().add("Custom-Button3");
                btn.setOnAction(load);
                box.getChildren().add(btn);
            }
            box.getStyleClass().add("Custom-BackGround");
            ScrollPane scroll = new ScrollPane();
            scroll.getStyleClass().add("Custom-Scroll");
            scroll.setContent(box);
            scroll.setFitToHeight(true);
            scroll.setFitToWidth(true);

            Button cancelButton = new Button(sectionGeneral.getValue("CANCEL_BUTTON_TEXT"));
            cancelButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
            cancelButton.getStyleClass().add("Custom-Button1");
            cancelButton.setOnAction(actionEvent -> dialogLoad.close());
            cancelButton.setAlignment(Pos.CENTER_RIGHT);

            infoBox = new HBox();
            infoBox.getChildren().add(cancelButton);

            VBox contentBox = new VBox();
            contentBox.setAlignment(Pos.TOP_CENTER);
            contentBox.getChildren().add(label);
            contentBox.getChildren().add(scroll);
            contentBox.getChildren().add(infoBox);

            BorderPane main = new BorderPane();
            main.setCenter(contentBox);
            main.getStyleClass().add("Custom-BackGround");
            mainPane.getChildren().add(main);
            AnchorPane.setBottomAnchor(main,0.0);
            AnchorPane.setTopAnchor(main,0.0);
            AnchorPane.setLeftAnchor(main,0.0);
            AnchorPane.setRightAnchor(main,0.0);
        }
    }

    /**
     * Show error text inside info box if needed.
     *
     * @param condition : Should the error message be shown.
     */
    private static void showErrorText(boolean condition){
        if (condition && infoBox.getChildren().size() == 1) {
            TextFlow textFlow = new TextFlow();
            Text text = new Text(sectionLoadProject.getValue("ERROR_TEXT"));
            text.getStyleClass().add("Custom-Error-Text");
            textFlow.getChildren().add(text);
            infoBox.getChildren().add(0, textFlow);
        }
        else if (!condition && infoBox.getChildren().size() == 2){
            infoBox.getChildren().remove(1);
        }
    }

    /**
     * Simple function for getting the project folders.
     *
     * @return : All the project folders.
     */
    static File[] checkProjects(){
        return projects.listFiles();
    }

}
