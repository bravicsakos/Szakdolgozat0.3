package sample;


import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


public class Controller {
    // @FXML változók ------------------------------------
    @FXML
    Button openfilebtn;
    @FXML
    ImageView mainimgview;
    @FXML
    ImageView smallimgview;
    @FXML
    Button exitbtn;
    @FXML
    Rectangle rectangle;
    @FXML
    Slider zoomslider;
    @FXML
    Text coordinates;
    @FXML
    BorderPane mainPane;
    @FXML
    ColorPicker colorPicker;



    // Egyéb változók ------------------------------------
    private FileChooser fileChooser = new FileChooser();

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private double rectorgTranslateX, rectorgTranslateY;

    private double smallScaleX, smallScaleY;

    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static double screenWidth = screenSize.getWidth();
    private final static double screenHeight = screenSize.getHeight();

    private final static File initialFile = new File("src");

    private static int snappedCounter = 1;


    public void initialize() {
        FileChooser.ExtensionFilter extensionFilterPNG = makeFilter("png");
        FileChooser.ExtensionFilter extensionFilterJPG = makeFilter("jpg");
        FileChooser.ExtensionFilter extensionFilterJPEG =  makeFilter("jpeg");

        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.RED);
        rectangle.setVisible(false);

        fileChooser.getExtensionFilters().addAll(extensionFilterJPG, extensionFilterPNG,extensionFilterJPEG);
        fileChooser.setSelectedExtensionFilter(extensionFilterJPEG);
        fileChooser.setTitle("Choose your file");
        fileChooser.setInitialDirectory(initialFile);

        coordinates.setFill(colorPicker.getValue());
    }

    public void handleOpenfilebtn(){
        File pictureFile = fileChooser.showOpenDialog(openfilebtn.getScene().getWindow());

        try {
            Image pictureImg = new Image(pictureFile.toURI().toURL().toString());
            mainimgview.setImage(pictureImg);
            smallimgview.setImage(pictureImg);
        }

        catch (MalformedURLException ex){
            System.err.println("Malformed URL Exception!");
        }

        mainimgview.setFitWidth(screenWidth);
        setFitHeight(mainimgview);

        smallimgview.setFitHeight(mainimgview.getImage().getHeight()/10);
        smallimgview.setFitWidth(mainimgview.getImage().getWidth()/10);
        smallScaleX = smallimgview.getFitWidth()/mainimgview.getFitWidth();
        smallScaleY = smallimgview.getFitHeight()/mainimgview.getFitHeight();

        rectangle.setVisible(true);
        rectangle.setHeight(smallimgview.getFitHeight());
        rectangle.setWidth(smallimgview.getFitWidth());
    }

    public void handleMouseClick(MouseEvent event){

        MouseButton buttonpressed = event.getButton();

        //Drag functions -----------------------------------------
        if (buttonpressed == MouseButton.PRIMARY) {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = mainimgview.getTranslateX();
            orgTranslateY = mainimgview.getTranslateY();
            rectorgTranslateX = rectangle.getTranslateX();
            rectorgTranslateY = rectangle.getTranslateY();
        }
        //--------------------------------------------------------

        //Snapshot functions--------------------------------------
        else if (buttonpressed == MouseButton.SECONDARY){
            int snapwidth = (int) Math.round(screenWidth);
            int snapheight = (int) Math.round(screenHeight);
            int snapMinX = (int) Math.round(event.getSceneX() - (screenWidth/2));
            int snapMinY = (int) Math.round(event.getSceneY() - (screenHeight/2));

            Rectangle2D snapshotBounds = new Rectangle2D(snapMinX,snapMinY,snapwidth,snapheight);
            SnapshotParameters snapParams = new SnapshotParameters();
            WritableImage snapImage = new WritableImage(snapwidth,snapheight);
            File snappedImage = new File("snap" + snappedCounter + ".png");

            snapParams.setViewport(snapshotBounds);
            snapImage = mainimgview.snapshot(snapParams,null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapImage, null), "png", snappedImage);
                snappedCounter++;
            }
            catch (IOException ex){
                System.err.println("IOException at take snapshot");
            }

        }
    }

    public void handleMouseDrag(MouseEvent event){
        double offsetX = event.getSceneX() - orgSceneX;
        double offsetY = event.getSceneY() - orgSceneY;
        double newTranslateX = orgTranslateX + offsetX;
        double newTranslateY = orgTranslateY + offsetY;
        double rectnewTranslateX = rectorgTranslateX - offsetX*smallScaleX*(1/mainimgview.getScaleX());
        double rectnewTranslateY = rectorgTranslateY - offsetY*smallScaleY*(1/mainimgview.getScaleX());

        if (!isFullSize(smallimgview,rectangle) && isInFocus(mainimgview,newTranslateX,newTranslateY)){

                mainimgview.setTranslateX(newTranslateX);
                mainimgview.setTranslateY(newTranslateY);
                rectangle.setTranslateX(rectnewTranslateX);
                rectangle.setTranslateY(rectnewTranslateY);
        }

    }

    public void handleScroll(ScrollEvent e){

        double scrollValue = e.getDeltaY();

        double oldtranslateX = mainimgview.getTranslateX();
        double oldtranslateY = mainimgview.getTranslateY();

        double mainimgScaleUpValue = 1.25;
        double mainimgScaleDownValue = 0.8;
        double rectScaleDownValue = 1/mainimgScaleUpValue;
        double rectScaleUpValue = 1/mainimgScaleDownValue;

        if (scrollValue>0 && rectangle.getWidth() > 10) {
            goToOrgPos(mainimgview);
            doScale(mainimgview,mainimgScaleUpValue);
            doScale(rectangle,rectScaleDownValue);
            moveObj(mainimgview,oldtranslateX*mainimgScaleUpValue,oldtranslateY*mainimgScaleUpValue);
            zoomslider.setValue(mainimgview.getScaleX());


        }
        else if(mainimgScaleUpValue*rectangle.getWidth() <= smallimgview.getFitWidth()){
            goToOrgPos(mainimgview);
            doScale(mainimgview,mainimgScaleDownValue);
            doScale(rectangle,rectScaleUpValue);
            moveObj(mainimgview,oldtranslateX*mainimgScaleDownValue,oldtranslateY*mainimgScaleDownValue);
            zoomslider.setValue(mainimgview.getScaleX());
            }

        else {
            goToOrgPos(mainimgview);
            goToOrgPos(rectangle);
            mainimgview.setScaleX(1);
            mainimgview.setScaleY(1);
            rectangle.setWidth(smallimgview.getFitWidth());
            rectangle.setHeight(smallimgview.getFitHeight());
            zoomslider.setValue(mainimgview.getScaleX());
        }

    }

    public void handleSlider(){
        double rectNewWidth = smallimgview.getFitWidth()*(1/zoomslider.getValue());
        double rectNewHeight = smallimgview.getFitHeight()*(1/zoomslider.getValue());


        mainimgview.setScaleX(zoomslider.getValue());
        mainimgview.setScaleY(zoomslider.getValue());
        rectangle.setWidth(rectNewWidth);
        rectangle.setHeight(rectNewHeight);
    }

    public void handleExitbtn(){
        Stage stage = (Stage) exitbtn.getScene().getWindow();
        stage.close();
    }

    public void handleMouseMovement(MouseEvent event){
        double mousePosComponentX = event.getSceneX()/mainimgview.getScaleX();
        double mousePosComponentY = event.getSceneY()/mainimgview.getScaleX();
        double scrollComponentX = screenWidth*(mainimgview.getScaleX()-1)/(2*mainimgview.getScaleX());
        double scrollComponentY = screenHeight*(mainimgview.getScaleY()-1)/(2*mainimgview.getScaleY());
        double dragComponentX = mainimgview.getTranslateX()*(1/mainimgview.getScaleX());
        double dragComponentY = mainimgview.getTranslateY()*(1/mainimgview.getScaleY());

        double mouseCoordX = mousePosComponentX + scrollComponentX - dragComponentX;
        double mouseCoordY = mousePosComponentY + scrollComponentY - dragComponentY;

        coordinates.setText("X Coordinate: " + Math.round(mouseCoordX) + "\nY Coordinate: " + Math.round(mouseCoordY));
    }

    public void handleColorPicker(){
        coordinates.setFill(colorPicker.getValue());
    }

    private static void doScale(ImageView imgview, double scale){
        //az adott objektumot méretarányt tartva adott scale-el növel
        imgview.setScaleX(imgview.getScaleX()*scale);
        imgview.setScaleY(imgview.getScaleY()*scale);
    }

    private static void doScale(Rectangle rect, double scale){
        //mint az imageviewnél (itt nem a scale változót növeljük)
        rect.setWidth(rect.getWidth()*scale);
        rect.setHeight(rect.getHeight()*scale);
    }

    private void goToOrgPos(ImageView imgview){
        //visszateszi az adott objektumot az eredeti pozíciójába
        imgview.setTranslateX(0);
        imgview.setTranslateY(0);
    }

    private void goToOrgPos(Rectangle rect){
        //mint az imageview esetén
        rect.setTranslateX(0);
        rect.setTranslateY(0);
    }

    private void moveObj(ImageView imgview, double posX,double posY){
        //adott objektumot adott pozícióba helyez
        imgview.setTranslateX(posX);
        imgview.setTranslateY(posY);
    }

    private void setFitHeight(ImageView imgview){
        //szükséges mert csak a fitWidth változót állítjuk be és a preserve ratio adja meg a height-ot azonban nem tárolja el a fitHeight
        imgview.setFitHeight ((imgview.getFitWidth()*imgview.getImage().getHeight())/imgview.getImage().getWidth());
    }

    private boolean isInFocus(ImageView imgview,double newTranslateX,double newTranslateY){
        //a megnyitott kép széleinek távolságát méri aképernyő széleitől, true ha nincs "fehér csík"
        double distanceX = 960-(((imgview.getFitWidth()/2)*imgview.getScaleX())-Math.abs(newTranslateX));
        double distanceY = 540-(((imgview.getFitHeight()/2)*imgview.getScaleY())-Math.abs(newTranslateY));

        if (distanceX>0){
            return false;
        }
        else if (distanceY>0){
            return false;
        }
        return true;
    }

    private boolean isFullSize(ImageView imgview,Rectangle rect){
        //ezzel állapítom meg hogy a kép teljesen ki van e nagyítva.. lehet mainimgview.scalex==1-el egyszerűbb lenne
        if (rect.getWidth()<imgview.getFitWidth()){
            return false;
        }
        return true;
    }

    private FileChooser.ExtensionFilter makeFilter(String extension){
        return new FileChooser.ExtensionFilter("." + extension,"*." + extension);
    }



}
