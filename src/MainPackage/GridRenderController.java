package MainPackage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import static MainPackage.ChooserScreenController.screenController;
import static MainPackage.IniTools.*;
import static MainPackage.PictureRenderer.*;
import static MainPackage.ThumbnailCreationController.*;
import static MainPackage.Tracer.*;
import static MainPackage.Constants.*;

public class GridRenderController {

    @FXML
    StackPane mainPane;

    protected static GridPane grid;

    private static VBox barHolder;

    private static VBox qualityBar;
    private static ToggleButton btn1024;
    private static ToggleButton btn512;
    private static ToggleButton btn256;
    private static ToggleButton btn128;
    private static ToggleButton btnRawImage;
    private static HashMap<ToggleButton, Integer> btnMap;
    static HashMap<Integer,ArrayList<ImageV2>> posMap;

    private static VBox changeButtonHolder;
    private static Button fullPictViewBtn;

    static ArrayList<ImageV2> position1;
    static ArrayList<ImageV2> position2;
    static ArrayList<ImageV2> position3;
    static ArrayList<ImageV2> position4;

    static int coordX = 0;
    static int coordY = 0;

    private Rectangle rect;
    private double rectSize;

    public void initialize(){

        for (int i = mainPane.getChildren().size()-1; i >= 0; i--) {
            mainPane.getChildren().remove(i);
        }
        reset();
        mainPane.getChildren().add(grid);

        makeGrid(100, 100);

        Button exitButton = new Button(settings.get("GENERAL","EXIT_BUTTON_TEXT"));
        exitButton.setOnAction(event -> System.exit(0));

        barHolder.getChildren().add(exitButton);
        barHolder.getChildren().add(qualityBar);
        barHolder.getChildren().add(changeButtonHolder);
        barHolder.setAlignment(Pos.TOP_RIGHT);

        mainPane.getChildren().add(barHolder);

        qualityBar.getChildren().addAll(btnRawImage, btn1024, btn512, btn256, btn128);
        qualityBar.setAlignment(Pos.TOP_RIGHT);

        changeButtonHolder.setAlignment(Pos.TOP_RIGHT);
        changeButtonHolder.getChildren().add(fullPictViewBtn);

        makeBtnHashmap();


        mainPane.getChildren().add(addSmallView());

        EventHandler btnEvent = event -> {
            qualityLvl = btnMap.get((ToggleButton) event.getTarget());
            coordX = position1.get(0).getCoordX();
            coordY = position1.get(0).getCoordY();
            switch (qualityLvl){
                case QUALITY_LEVEL_RAWIMAGE :
                    rect.setHeight(rectSize);
                    rect.setWidth(rectSize);
                    break;
                case QUALITY_LEVEL_1024 :
                    rect.setHeight(rectSize*2);
                    rect.setWidth(rectSize*2);
                    break;
                    case QUALITY_LEVEL_512 :
                    rect.setHeight(rectSize*4);
                    rect.setWidth(rectSize*4);
                    break;
                case QUALITY_LEVEL_256 :
                    rect.setHeight(rectSize*8);
                    rect.setWidth(rectSize*8);
                    break;
                case QUALITY_LEVEL_128 :
                    rect.setHeight(rectSize*16);
                    rect.setWidth(rectSize*16);
                    break;
            }
            qualityLvlManager();
            for (Object tb: qualityBar.getChildren()) {
                ((ToggleButton) tb).setSelected(false);
            }
            ((ToggleButton) event.getTarget()).setSelected(true);

        };
        for (Object tb: qualityBar.getChildren()) {
            //noinspection unchecked
            ((ToggleButton) tb).setOnAction(btnEvent);
        }
        fullPictViewBtn.setOnAction(event -> screenController.setScreen(mainPane.getScene(), "LowQualityRender.fxml"));

        posMap.put(POS_UPLEFT,position1);
        posMap.put(POS_UPRIGHT,position2);
        posMap.put(POS_DOWNLEFT,position3);
        posMap.put(POS_DOWNRIGHT,position4);
        makeQualityLevels();
        qualityLvl = QUALITY_LEVEL_128;
        btn128.setSelected(true);
        PictureRenderer.qualityLvlManager();

    }


    public void handleKeyPush(KeyEvent event){
        switch (event.getCode()){
            case UP:
                PictureRenderer.inputHandler(INPUT_UP,qualityMap.get(qualityLvl));
                break;
            case DOWN:
                PictureRenderer.inputHandler(INPUT_DOWN,qualityMap.get(qualityLvl));
                break;
            case LEFT:
                PictureRenderer.inputHandler(INPUT_LEFT,qualityMap.get(qualityLvl));
                break;
            case RIGHT:
                PictureRenderer.inputHandler(INPUT_RIGHT,qualityMap.get(qualityLvl));
                break;
        }
    }

    static boolean isRenderable(int coordinateX, int coordinateY){
        int id = PictureRenderer.computeImageID(coordinateX,coordinateY);
        return id >= 0 && id < imageListSize;
    }

    private static void makeBtnHashmap(){
        btnMap.put(btnRawImage,QUALITY_LEVEL_RAWIMAGE);
        btnMap.put(btn1024,QUALITY_LEVEL_1024);
        btnMap.put(btn512,QUALITY_LEVEL_512);
        btnMap.put(btn256,QUALITY_LEVEL_256);
        btnMap.put(btn128,QUALITY_LEVEL_128);
    }

    private static void reset(){
        grid = new GridPane();

        barHolder = new VBox();

        qualityBar = new VBox();
        btn1024 = new ToggleButton(settings.get("GRID_RENDER","BUTTON_1024_TEXT"));
        btn512 = new ToggleButton(settings.get("GRID_RENDER","BUTTON_512_TEXT"));
        btn256 = new ToggleButton(settings.get("GRID_RENDER","BUTTON_256_TEXT"));
        btn128 = new ToggleButton(settings.get("GRID_RENDER","BUTTON_128_TEXT"));
        btnRawImage = new ToggleButton(settings.get("GRID_RENDER","BUTTON_RAW_IMAGE_TEXT"));
        btnMap = new HashMap<>();
        posMap = new HashMap<>();

        changeButtonHolder = new VBox();
        fullPictViewBtn = new Button(settings.get("GRID_RENDER","FULL_PICTURE_BUTTON_TEXT"));

        position1 = new ArrayList<>();
        position2 = new ArrayList<>();
        position3 = new ArrayList<>();
        position4 = new ArrayList<>();
    }

    private StackPane addSmallView(){
        StackPane smallView = new StackPane();
        File temp = mrxsFile;
        ImageView mrxsView = new ImageView();
        try{
            Image mrxsImage = new Image(temp.toURI().toURL().toString());
            mrxsView.setImage(mrxsImage);
            mrxsView.setRotate(270);
        }
        catch (MalformedURLException ex){
            System.err.println("Malformed URL at addMrxs");
        }
        mrxsView.setFitWidth(350);
        mrxsView.setFitHeight((mrxsView.getFitWidth()*mrxsView.getImage().getHeight())/mrxsView.getImage().getWidth());
        smallView.setTranslateX((screenWidth - mrxsView.getFitHeight())/2);
        smallView.setTranslateY((screenHeigth - mrxsView.getFitWidth())/2);
        mrxsView.setPreserveRatio(true);
        rectSize = mrxsView.getFitWidth()/100;
        rect = new Rectangle(mrxsView.getTranslateX()-(mrxsView.getFitWidth()/2),mrxsView.getTranslateY()-(mrxsView.getFitHeight()/2),rectSize*16,rectSize*16);
        rect.setTranslateX(-390);
        rect.setTranslateY(-145);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.RED);
        smallView.getChildren().add(mrxsView);
        smallView.getChildren().add(rect);
        return smallView;

    }

}
