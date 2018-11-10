package MainPackage;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static MainPackage.Constants.*;
import static MainPackage.Main.pStage;
import static MainPackage.Main.sectionGeneral;
import static MainPackage.PictureRenderer.*;


/**
 * Class for creating the main page of the program.
 *
 * @variable mainPane : The main pane for the window.
 *
 * @variable sectionMainPane : Main pane section inside settings.ini.
 * @variable sectionThumbnail : Thumbnail section inside settings.ini.
 *
 * @variable dialogNew,
 *           dialogLoad,
 *           dialogInstall : Stages for the pop up windows named accordingly.
 *
 * @variable thumbnailPane : Main pane for thumbnail creation.
 * @variable progressTab : Contains the thumbnail creation texts.
 * @variable progressText : Writes the current progress of thumbnail creation.
 * @variable pleaseWaitText : Basic information text.
 * @variable stopperText : Time spent on creation.
 *
 * @variable projectFolder : Folder of the current open project.
 *
 * @variable main : Main pane of the window.
 * @variable headerBoxLeft : Box containing the buttons.
 * @variable projectText : Text containing the program name and the current open project.
 * @variable finishedButton : Button shown when thumbnail creation is finished.
 *
 * @variable grid : Pane containing the rendered pictures
 * @variable isReady : Condition for determining if the rendered images are ready for move input.
 *
 * @variable orgSceneX,
 *           orgSceneY : Original position of mouse cursor.
 * @variable orgTranslateX,
 *           orgTranslateY : Original positions of whole picture.
 *
 * @variable buttonNewProject : Button for creating a new project.
 *
 * @variable singleImageFile : File for the single whole image.
 */
public class MainPageController {
    // TODO : General UI improvements

    @FXML
    StackPane mainPane;

    private static IniTools sectionMainPane = new IniTools("MAIN_PANE");
    static IniTools sectionThumbnail = new IniTools("THUMBNAIL_CREATION");

    static Stage dialogNew;
    static Stage dialogLoad;
    static Stage dialogInstall;

    static BorderPane thumbnailPane = new BorderPane();
    private static TextFlow progressTab = new TextFlow();
    static Text progressText = new Text();
    static Text pleaseWaitText = new Text();
    static Text stopperText = new Text();

    static File projectFolder;

    protected static BorderPane main;
    private static HBox headerBoxLeft;
    private static Text projectText;
    static Button finishedButton;

    static GridPane grid = new GridPane();
    private static boolean isReady = false;

    private static double orgSceneX, orgSceneY;
    private static double orgTranslateX, orgTranslateY;

    static Button btnNewProject;

    static File singleImageFile;

    public void initialize(){
        main = createLayout();
        mainPane.getChildren().add(main);
    }

    /**
     * Method for creating the layout of the window
     *
     * @return : the created main pane.
     */
    private BorderPane createLayout(){
        /*Stage customize             */
        dialogNew = new Stage();
        dialogLoad = new Stage();
        dialogInstall = new Stage();
        dialogNew.initStyle(StageStyle.TRANSPARENT);
        dialogNew.initModality(Modality.APPLICATION_MODAL);
        dialogNew.initOwner(pStage);
        dialogLoad.initStyle(StageStyle.TRANSPARENT);
        dialogLoad.initModality(Modality.APPLICATION_MODAL);
        dialogLoad.initOwner(pStage);
        dialogInstall.initStyle(StageStyle.TRANSPARENT);
        dialogInstall.initModality(Modality.APPLICATION_MODAL);
        dialogInstall.initOwner(pStage);

        /*Header construct:          */
        //New Project Button
        btnNewProject = new Button(sectionMainPane.getValue("BUTTON_NEW_PROJECT_TEXT"));
        btnNewProject.getStyleClass().add("Custom-Button1");
        btnNewProject.setOnAction(actionEvent -> {
            if (sectionGeneral.getValue("PANORAMIC_VIEWER_PATH") == null){
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("InstallViewer.fxml"));
                    Scene scene = new Scene(root, 300, 200);
                    scene.getStylesheets().add(sectionGeneral.getValue("STYLING_FILE_NAME"));
                    dialogInstall.setScene(scene);
                    dialogInstall.sizeToScene();
                    dialogInstall.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (!InstallViewerController.confirmFile()){
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("InstallViewer.fxml"));
                    InstallViewerController.infoText.setText("File confirm failed, file doesn't exist\n or not named 'MView.exe'.");
                    Scene scene = new Scene(root, 300, 200);
                    scene.getStylesheets().add(sectionGeneral.getValue("STYLING_FILE_NAME"));
                    dialogInstall.setScene(scene);
                    dialogInstall.sizeToScene();
                    dialogInstall.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("NewProject.fxml"));
                    Scene scene = new Scene(root, 300, 150);
                    scene.getStylesheets().add(sectionGeneral.getValue("STYLING_FILE_NAME"));
                    dialogNew.setScene(scene);
                    dialogNew.sizeToScene();
                    dialogNew.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            });

        //Load Project Button
        Button btnLoadProject = new Button(sectionMainPane.getValue("BUTTON_LOAD_PROJECT_TEXT"));
        btnLoadProject.getStyleClass().add("Custom-Button1");
        btnLoadProject.setOnAction(actionEvent -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("LoadProject.fxml"));
                Scene scene = new Scene(root, 300, 200);
                scene.getStylesheets().add(sectionGeneral.getValue("STYLING_FILE_NAME"));
                dialogLoad.setScene(scene);
                dialogLoad.show();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        });

        Button singlePicture = new Button(sectionMainPane.getValue("SINGLE_PICTURE_VIEWER"));
        singlePicture.getStyleClass().add("Custom-Button1");
        singlePicture.setOnAction(actionEvent -> {
            FileChooser fc = new FileChooser();
            singleImageFile = fc.showOpenDialog(singlePicture.getScene().getWindow());
            ScreenController sc = new ScreenController();
            sc.setScreen(singlePicture.getScene(),"SinglePictureRender.fxml");
        });

        //Exit Button
        Button btnExit = new Button(sectionGeneral.getValue("EXIT_BUTTON_TEXT"));
        btnExit.getStyleClass().add("Custom-Button1");
        btnExit.setOnAction(actionEvent -> System.exit(0));

        //Minimize Button
        Button btnMinimize = new Button(sectionGeneral.getValue("MINIMIZE_BUTTON_TEXT"));
        btnMinimize.getStyleClass().add("Custom-Button1");
        btnMinimize.setOnAction(actionEvent -> pStage.setIconified(true));

        //Project text
        projectText = new Text();
        constructProjectText("No Project Selected");
        projectText.getStyleClass().add("Custom-Text3");

        //Adding Buttons to header
        headerBoxLeft = new HBox();
        HBox headerBoxRight = new HBox();

        headerBoxLeft.getChildren().add(btnNewProject);
        headerBoxLeft.getChildren().add(btnLoadProject);
        headerBoxLeft.getChildren().add(singlePicture);
        headerBoxRight.getChildren().add(btnMinimize);
        headerBoxRight.getChildren().add(btnExit);

        BorderPane headerPane = new BorderPane();

        headerPane.getStyleClass().add("Custom-HeadBoard");
        headerPane.setLeft(headerBoxLeft);
        headerPane.setRight(headerBoxRight);
        headerPane.setCenter(projectText);

        /*Thumbnail creation screen construct   */
        pleaseWaitText.setText(sectionThumbnail.getValue("PLEASE_WAIT_TEXT_BASE") + ".");
        pleaseWaitText.getStyleClass().add("Custom-Text2");
        progressText.getStyleClass().add("Custom-Text2");
        stopperText.getStyleClass().add("Custom-Text2");

        progressTab.getStyleClass().add("Custom-Text2");
        progressTab.getChildren().add(pleaseWaitText);
        progressTab.getChildren().add(progressText);
        progressTab.getChildren().add(stopperText);

        finishedButton = new Button();
        finishedButton.setText(sectionMainPane.getValue("FINISHED_BUTTON_TEXT"));
        finishedButton.getStyleClass().add("Custom-Button5");
        finishedButton.setOnAction(actionEvent -> constructLowQualityViewer());
        finishedButton.setVisible(false);
        finishedButton.setDisable(true);

        thumbnailPane = new BorderPane();
        thumbnailPane.getStyleClass().add("Custom-BackGround");
        thumbnailPane.setPadding(new Insets(20));
        thumbnailPane.setTop(progressTab);
        thumbnailPane.setCenter(finishedButton);

        /*Layout construct:           */
        BorderPane main = new BorderPane();
        main.getStyleClass().add("Custom-BackGround");
        main.setTop(headerPane);

        return main;
    }

    /**
     * Enables/disables the buttons on the left
     * of the header to prevent bugs.
     *
     * @param condition : If the buttons should be enabled.
     */
    static void setEnabled(boolean condition){
        headerBoxLeft.setDisable(!condition);
    }

    /**
     * Constructs the main text for the window
     *
     * @param projectName : The name of the currently open project.
     * @uses : General section / app name,
     *         General section / app version from settings.ini.
     */
    static void constructProjectText(String projectName){
        String stringBuilder = sectionGeneral.getValue("APP_NAME") +
                " ver. " +
                sectionGeneral.getValue("APP_VERSION") +
                "    ---    " +
                projectName;
        projectText.setText(stringBuilder);
    }

    /**
     * Constructs the viewer for the whole
     * low quality image.
     */
    private static void constructLowQualityViewer(){
        isReady = false;

        /*Low quality render            */
        Button gridViewButton = new Button();
        gridViewButton.setText(sectionMainPane.getValue("GRID_VIEW_BUTTON_TEXT"));
        gridViewButton.getStyleClass().add("Custom-Button4");
        gridViewButton.setOnAction(actionEvent -> {
            constructGridViewer();
            PictureRenderer.run();
        });

        File mrxsFile = new File(projectFolder + "\\" + sectionMainPane.getValue("MRXS_FILE_NAME"));
        ImageView imageView = new ImageView();
        try {
            BufferedImage inputImage = ImageIO.read(mrxsFile);
            BufferedImage outputImage = null;
            outputImage = Scalr.rotate(inputImage, Scalr.Rotation.CW_90);
            Image mrxsImage = SwingFXUtils.toFXImage(outputImage,null);
            imageView.setImage(mrxsImage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        VBox vBox1 = new VBox();
        vBox1.setPadding(new Insets(20));
        vBox1.setPrefHeight(1000);

        VBox vBox2 = new VBox();
        vBox2.setSpacing(20);
        vBox2.getChildren().add(gridViewButton);
        vBox2.getStyleClass().add("Custom-Menu");
        vBox2.setPrefHeight(1000);
        vBox2.setPadding(new Insets(10));
        vBox1.getChildren().add(vBox2);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(10));
        stackPane.getChildren().add(imageView);
        stackPane.setOnMousePressed(mouseEvent -> {
            orgSceneX = mouseEvent.getSceneX();
            orgSceneY = mouseEvent.getSceneY();
            orgTranslateX = imageView.getTranslateX();
            orgTranslateY = imageView.getTranslateY();
        });
        stackPane.setOnMouseDragged(mouseEvent -> {
            double offsetX = mouseEvent.getSceneX() - orgSceneX;
            double offsetY = mouseEvent.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            if (newTranslateX >= -190 && newTranslateX <= 20) {
                imageView.setTranslateX(newTranslateX);
            }
            if  (newTranslateY >= -200 && newTranslateY <= 200) {
                imageView.setTranslateY(newTranslateY);
            }
        });

        main.setCenter(stackPane);
        main.setLeft(vBox1);
    }

    /**
     * Constructs the viewer for the
     * grid based images.
     */
    private static void constructGridViewer(){
        // TODO : something to go on the right side
        Button fullPictView = new Button();
        fullPictView.setText(sectionMainPane.getValue("FULL_PICTURE_BUTTON_TEXT"));
        fullPictView.getStyleClass().add("Custom-Button4");
        fullPictView.setOnAction(actionEvent -> {
            constructLowQualityViewer();
        });

        ToggleButton btnRaw = new ToggleButton();
        btnRaw.setText(sectionMainPane.getValue("BUTTON_RAW_IMAGE_TEXT"));
        btnRaw.getStyleClass().add("Custom-Toggle-Button");

        ToggleButton btn1024 = new ToggleButton();
        btn1024.setText(sectionMainPane.getValue("BUTTON_1024_TEXT"));
        btn1024.getStyleClass().add("Custom-Toggle-Button");

        ToggleButton btn512 = new ToggleButton();
        btn512.setText(sectionMainPane.getValue("BUTTON_512_TEXT"));
        btn512.getStyleClass().add("Custom-Toggle-Button");

        ToggleButton btn256 = new ToggleButton();
        btn256.setText(sectionMainPane.getValue("BUTTON_256_TEXT"));
        btn256.getStyleClass().add("Custom-Toggle-Button");

        ToggleButton btn128 = new ToggleButton();
        btn128.setText(sectionMainPane.getValue("BUTTON_128_TEXT"));
        btn128.getStyleClass().add("Custom-Toggle-Button");
        btn128.setSelected(true);

        VBox vBox1 = new VBox();
        vBox1.setPadding(new Insets(20));
        vBox1.setPrefHeight(1000);

        VBox vBox2 = new VBox();
        vBox2.setSpacing(20);
        vBox2.getChildren().addAll(fullPictView, btnRaw, btn1024, btn512, btn256, btn128);
        vBox2.getStyleClass().add("Custom-Menu");
        vBox2.setPrefHeight(1000);
        vBox2.setPadding(new Insets(10));
        vBox1.getChildren().add(vBox2);

        btnRaw.setOnAction(actionEvent -> {
            qualityLvl = QUALITY_LEVEL_RAWIMAGE;
            coordX = position1.get(0).getCoordX();
            coordY = position1.get(0).getCoordY();
            qualityLvlManager();
            for (Object tb: vBox2.getChildren()) {
                if (tb instanceof ToggleButton) {
                    ((ToggleButton) tb).setSelected(false);
                }
            }
            ((ToggleButton) actionEvent.getTarget()).setSelected(true);
        });

        btn1024.setOnAction(actionEvent -> {
            qualityLvl = QUALITY_LEVEL_1024;
            coordX = position1.get(0).getCoordX();
            coordY = position1.get(0).getCoordY();
            qualityLvlManager();
            for (Object tb: vBox2.getChildren()) {
                if (tb instanceof ToggleButton) {
                    ((ToggleButton) tb).setSelected(false);
                }
            }
            ((ToggleButton) actionEvent.getTarget()).setSelected(true);
        });

        btn512.setOnAction(actionEvent -> {
            qualityLvl = QUALITY_LEVEL_512;
            coordX = position1.get(0).getCoordX();
            coordY = position1.get(0).getCoordY();
            qualityLvlManager();
            for (Object tb: vBox2.getChildren()) {
                if (tb instanceof ToggleButton) {
                    ((ToggleButton) tb).setSelected(false);
                }
            }
            ((ToggleButton) actionEvent.getTarget()).setSelected(true);
        });

        btn256.setOnAction(actionEvent -> {
            qualityLvl = QUALITY_LEVEL_256;
            coordX = position1.get(0).getCoordX();
            coordY = position1.get(0).getCoordY();
            qualityLvlManager();
            for (Object tb: vBox2.getChildren()) {
                if (tb instanceof ToggleButton) {
                    ((ToggleButton) tb).setSelected(false);
                }
            }
            ((ToggleButton) actionEvent.getTarget()).setSelected(true);
        });

        btn128.setOnAction(actionEvent -> {
            qualityLvl = QUALITY_LEVEL_128;
            coordX = position1.get(0).getCoordX();
            coordY = position1.get(0).getCoordY();
            qualityLvlManager();
            for (Object tb: vBox2.getChildren()) {
                if (tb instanceof ToggleButton) {
                    ((ToggleButton) tb).setSelected(false);
                }
            }
            ((ToggleButton) actionEvent.getTarget()).setSelected(true);
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(grid);
        stackPane.setPadding(new Insets(20));

        main.setLeft(vBox1);
        main.setCenter(stackPane);
        isReady = true;
    }

    /**
     * Handler for key press used to move the
     * grid based images
     *
     * @param keyEvent : The specific key pressed.
     */
    public void handleKeyPressed(KeyEvent keyEvent){
        if (isReady) {
            if (keyEvent.getCode() == KeyCode.UP){
                PictureRenderer.inputHandler(INPUT_UP, qualityMap.get(qualityLvl));
            }
            else if (keyEvent.getCode() == KeyCode.DOWN){
                PictureRenderer.inputHandler(INPUT_DOWN, qualityMap.get(qualityLvl));
            }
            else if (keyEvent.getCode() == KeyCode.LEFT){
                PictureRenderer.inputHandler(INPUT_LEFT, qualityMap.get(qualityLvl));
            }
            else if (keyEvent.getCode() == KeyCode.RIGHT){
                PictureRenderer.inputHandler(INPUT_RIGHT, qualityMap.get(qualityLvl));
            }
        }
    }

}
