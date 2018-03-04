package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;


public class Controller {
    // @FXML változók ------------------------------------
    @FXML
    Button openfilebtn = new Button();
    @FXML
    ImageView mainimgview = new ImageView();
    @FXML
    ImageView smallimgview = new ImageView();
    @FXML
    Button exitbtn = new Button();
    @FXML
    Rectangle rectangle;
    @FXML
    Slider zoomslider;



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
        orgSceneX = event.getSceneX();
        orgSceneY = event.getSceneY();
        orgTranslateX = mainimgview.getTranslateX();
        orgTranslateY = mainimgview.getTranslateY();
        rectorgTranslateX = rectangle.getTranslateX();
        rectorgTranslateY = rectangle.getTranslateY();

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
