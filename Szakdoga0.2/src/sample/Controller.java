package sample;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
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
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extensionFilterPNG = new FileChooser.ExtensionFilter(".png","*.png");
    FileChooser.ExtensionFilter extensionFilterJPG = new FileChooser.ExtensionFilter(".jpg","*.jpg");
    FileChooser.ExtensionFilter extensionFilterJPEG = new FileChooser.ExtensionFilter(".jpeg","*.jpeg");

    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    double rectorgTranslateX, rectorgTranslateY;

    double smallScaleX, smallScaleY;

    public void initialize() {

        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.RED);
        rectangle.setVisible(false);
        fileChooser.getExtensionFilters().addAll(extensionFilterJPG, extensionFilterPNG,extensionFilterJPEG);
        fileChooser.setSelectedExtensionFilter(extensionFilterJPEG);
        fileChooser.setTitle("Choose your file");
        File initialFile = new File("src");
        fileChooser.setInitialDirectory(initialFile);
    }

    public void handleOpenfilebtn(){
        File pictureFile = fileChooser.showOpenDialog(openfilebtn.getScene().getWindow());
        try {
            Image pictureImg = new Image(pictureFile.toURI().toURL().toString());
            mainimgview.setImage(pictureImg);
            smallimgview.setImage(pictureImg);
            mainimgview.setFitWidth(1920);
            mainimgview.setPreserveRatio(true);
            mainimgview.toBack();
            StackPane.setAlignment(mainimgview, Pos.CENTER);
            rectangle.setVisible(true);
            smallimgview.setFitHeight(mainimgview.getImage().getHeight()/10);
            smallimgview.setFitWidth(mainimgview.getImage().getWidth()/10);
            rectangle.setHeight(smallimgview.getFitHeight());
            rectangle.setWidth(smallimgview.getFitWidth());
            smallScaleX = smallimgview.getFitWidth()/mainimgview.getFitWidth();
            smallScaleY = smallimgview.getFitHeight()/(mainimgview.getFitWidth()/mainimgview.getImage().getWidth()*mainimgview.getImage().getHeight());

        }
        catch (MalformedURLException ex){
            System.err.println("Malformed URL Exception!");
        }
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

        if (rectangle.getWidth()<smallimgview.getFitWidth() &&
            960-(mainimgview.getImage().getWidth()/2*mainimgview.getScaleX()-Math.abs(newTranslateX))<=0 &&
            540-(mainimgview.getImage().getHeight()/2*mainimgview.getScaleY()-Math.abs(newTranslateY))<=0){

                mainimgview.setTranslateX(newTranslateX);
                mainimgview.setTranslateY(newTranslateY);
                rectangle.setTranslateX(rectnewTranslateX);
                rectangle.setTranslateY(rectnewTranslateY);
        }

    }

    public void handleScroll(ScrollEvent e){
        if (e.getDeltaY()>0 && rectangle.getWidth() > 10) {
            mainimgview.setScaleX(mainimgview.getScaleX()*1.25);
            mainimgview.setScaleY(mainimgview.getScaleY()*1.25);
            rectangle.setWidth(rectangle.getWidth()*0.8);
            rectangle.setHeight(rectangle.getHeight()*0.8);
            zoomslider.setValue(mainimgview.getScaleX());
        }
        else if(1.25*rectangle.getWidth() <= smallimgview.getFitWidth()){
            mainimgview.setScaleX(mainimgview.getScaleX()*0.8);
            mainimgview.setScaleY(mainimgview.getScaleY()*0.8);
            rectangle.setWidth(rectangle.getWidth()*1.25);
            rectangle.setHeight(rectangle.getHeight()*1.25);
            zoomslider.setValue(mainimgview.getScaleX());
        }
        else {
            mainimgview.setTranslateX(0);
            mainimgview.setTranslateY(0);
            rectangle.setTranslateX(0);
            rectangle.setTranslateY(0);
            mainimgview.setScaleX(1);
            mainimgview.setScaleY(1);
            rectangle.setWidth(smallimgview.getFitWidth());
            rectangle.setHeight(smallimgview.getFitHeight());
            zoomslider.setValue(mainimgview.getScaleX());
        }

    }

    public void handleSlider(){
        mainimgview.setScaleX(zoomslider.getValue());
        mainimgview.setScaleY(zoomslider.getValue());
        rectangle.setWidth(1/zoomslider.getValue());
        rectangle.setHeight(1/zoomslider.getValue());
    }

    public void handleExitbtn(){
        Stage stage = (Stage) exitbtn.getScene().getWindow();
        stage.close();
    }

}
