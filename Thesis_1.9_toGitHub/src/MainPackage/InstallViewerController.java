package MainPackage;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static MainPackage.IniTools.settings;
import static MainPackage.Main.sectionGeneral;
import static MainPackage.MainPageController.btnNewProject;
import static MainPackage.MainPageController.dialogInstall;
import static MainPackage.MainPageController.makeCancelButton;


/**
 * Class for missing Panoramic Viewer install window.
 *
 * variable mainPane : Main pane inside FXML file ( InstallViewer.fxml ).
 * variable sectionInstallViewer : Install viewer section inside settings.ini.
 * variable fileChooser : File chooser for browsing 'MView.exe'.
 * variable viewerFile : File for the Panoramic Viewer.
 * variable infoText : Text for specific info about errors.
 * */
public class InstallViewerController{
    @FXML
    StackPane mainPane;

    private static IniTools sectionInstallViewer = new IniTools("INSTALL_VIEWER");
    private static FileChooser fileChooser = new FileChooser();
    private static File viewerFile;

    static Text infoText;

    /**
     * Creating the window and adding functionality
     */
    public void initialize(){
        mainPane.getStyleClass().add("Custom-BorderOnly");

        VBox contentBox = makeWindow();

        mainPane.getChildren().add(contentBox);
    }

    @NotNull
    private static VBox makeWindow() {
        infoText = new Text();
        infoText.setText("File location not found!");
        infoText.getStyleClass().add("Custom-Error-Text");

        Text infoLabel = new Text();
        infoLabel.setText(sectionInstallViewer.getValue("LABEL_TEXT"));
        infoLabel.getStyleClass().add("Custom-Error-Text");

        Button install = makeInstallButton();
        Button browse = makeBrowseButton();
        Button search = makeSearchButton();
        Button cancel = makeCancelButton(dialogInstall);

        VBox buttonBox = new VBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(install, browse, search);
        buttonBox.setTranslateY(buttonBox.getTranslateY() + 10);

        BorderPane messageBox = new BorderPane();
        messageBox.setLeft(infoText);
        messageBox.setRight(cancel);
        messageBox.setTranslateY(buttonBox.getTranslateY() + 10);

        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(10));
        contentBox.getChildren().addAll(infoLabel, buttonBox, messageBox);
        return contentBox;
    }

    @NotNull
    private static Button makeSearchButton() {
        Button search = new Button();
        search.setText(sectionInstallViewer.getValue("SEARCH_BUTTON_TEXT"));
        search.getStyleClass().add("Custom-Button3");
        search.setOnAction(actionEvent -> {
            FileSearch.fileName = "MView.exe";
            File[] roots = File.listRoots();
            for (File root : roots) {
                FileSearch.search(root);
                if (FileSearch.result != null){
                    break;
                }
            }
            if (FileSearch.result != null){
                try {
                    settings.put("GENERAL", "PANORAMIC_VIEWER_PATH", FileSearch.result.getPath());
                    settings.store();
                    dialogInstall.close();
                    btnNewProject.fire();

                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            else {
                infoText.setText("Program could not find the file!");
            }
        });
        return search;
    }

    @NotNull
    private static Button makeBrowseButton() {
        Button browse = new Button();
        browse.setText(sectionInstallViewer.getValue("BROWSE_BUTTON_TEXT"));
        browse.getStyleClass().add("Custom-Button3");
        browse.setOnAction(actionEvent -> {
            viewerFile = fileChooser.showOpenDialog(browse.getScene().getWindow());
            if (viewerFile != null) {
                try {
                    settings.put("GENERAL", "PANORAMIC_VIEWER_PATH", viewerFile.getPath());
                    settings.store();
                    dialogInstall.close();
                    btnNewProject.fire();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return browse;
    }

    @NotNull
    private static Button makeInstallButton() {
        Button install = new Button();
        install.setText(sectionInstallViewer.getValue("INSTALL_BUTTON_TEXT"));
        install.getStyleClass().add("Custom-Button3");
        install.setOnAction(actionEvent -> {
            try {
                Process process = new ProcessBuilder(System.getProperty("user.dir") + "\\Pannoramic_Viewer_1.15.4__RTM__v1.15.4.43061.exe").start();
                process.waitFor();
                infoText.setText("If installation is successful browse,\n or search for file");
            }catch (IOException ex){
                infoText.setText("You don't have priviliges to run installer!\n Please run the program as administrator");
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }


        });
        return install;
    }

    /**Method for confirming Panoramic Viewer executable
     *
     * @return True/False whether the viewer file exists
     */
    static boolean confirmFile(){
        File viewerFile = new File(sectionGeneral.getValue("PANORAMIC_VIEWER_PATH"));
        return viewerFile.exists() && viewerFile.getName().equals("MView.exe");
    }
}
