package MainPackage;

import com.sun.javafx.tools.packager.MakeAllParams;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Hashtable;


public class SinglePictureRenderController {
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
    Canvas canvasGo;
    @FXML
    Button drawbutton;
    @FXML
    Text coordinates;
    @FXML
    BorderPane mainPane;
    @FXML
    ColorPicker colorPicker;




    // Egyéb változók ------------------------------------
    private FileChooser fileChooser = new FileChooser();
    private final static File initialFile = new File("src");

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private double rectorgTranslateX, rectorgTranslateY;

    private double smallScaleX, smallScaleY;

    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static double screenWidth = screenSize.getWidth();
    private final static double screenHeight = screenSize.getHeight();
    //Draw változók-------------------------------------
    private boolean isDrawMode = false;
    private GraphicsContext gcF;
    private double  lastX,lastY,oldX,oldY;
    //Snap változók-------------------------------------
    private static int snappedCounter=1;
    private static ArrayList<SnappedImage> snapList = new ArrayList<>();
    private final static File snapLog = new File("C:\\Users\\qwerty\\IdeaProjects\\szakdogalivetestsmall\\src\\MainPackage\\SnapLog.txt");
    private final static String seperationString = " ";
    private static int snapMinXCoord = 0;
    private static int snapMinYCoord = 0;
    private static int snapMaxXCoord = 0;
    private static int snapMaxYCoord = 0;

    CoordinateGrid coordinateGrid = null;

    public void initialize() {

        File imageFile = PictureChooserController.imageFile;
        try {
            ImageV2 image = new ImageV2(imageFile.toURI().toURL().toString());
            mainimgview.setImage(image);
            smallimgview.setImage(image);
        }
        catch (MalformedURLException ex){
            System.err.println("Malformed URL");
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

        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.RED);
        rectangle.setVisible(true);

        gcF = canvasGo.getGraphicsContext2D();
        canvasGo.setVisible(false);

        coordinates.setFill(colorPicker.getValue());

        snappedImgLoad(snapList);

        coordinateGrid = new CoordinateGrid(mainimgview.getFitWidth(),mainimgview.getFitHeight(),screenWidth,screenHeight);
        coordinateGrid.yOffest = (screenHeight - mainimgview.getFitHeight())/2;
    }

    public void handleMouseClick(MouseEvent event) {
        MouseButton buttonpressed = event.getButton();

        if (isDrawMode) {
            this.oldX = event.getX();
            this.oldY = event.getY();
        } else
        {
            if (buttonpressed == MouseButton.PRIMARY) {
                orgSceneX = event.getSceneX();
                orgSceneY = event.getSceneY();
                orgTranslateX = mainimgview.getTranslateX();
                orgTranslateY = mainimgview.getTranslateY();
                rectorgTranslateX = rectangle.getTranslateX();
                rectorgTranslateY = rectangle.getTranslateY();
            }
            //Snapshot functions--------------------------------------
            else if (buttonpressed == MouseButton.SECONDARY){
                int snapwidth = (int) Math.round(screenWidth);
                int snapheight = (int) Math.round(screenHeight);
                int snapMinX = 0 ;
                int snapMinY = 0 ;

                getSnapCoords();
                SnappedImage snappedImage = new SnappedImage(snappedCounter,snapMinXCoord,snapMinYCoord,snapMaxXCoord,snapMaxYCoord);
                snapList.add(snappedImage);
                snappedImgSave(snapList);

                Rectangle2D snapshotBounds = new Rectangle2D(snapMinX,snapMinY,snapwidth,snapheight);
                SnapshotParameters snapParams = new SnapshotParameters();
                WritableImage snapImage = new WritableImage(snapwidth,snapheight);
                File snappedImageFile = FileStructure.makeFile(snappedImage);
                FileStructure.fileLogger(snappedImage);
                FileStructure.folderLogger();

                snapParams.setViewport(snapshotBounds);
                snapImage = mainimgview.snapshot(snapParams,null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapImage, null), "png", snappedImageFile);
                    snappedCounter++;
                }
                catch (IOException ex){
                    System.err.println("IOException at take snapshot");
                }
                System.out.println(mainimgview.getTranslateX());
                System.out.println(mainimgview.getTranslateY());
            }
        }
    }

    public void handleMouseDrag(MouseEvent event){

        if(isDrawMode)
        {
            this.lastX = event.getX();
            this.lastY = event.getY();
            freeDrawing();
        }else
        {
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
    }

    public void handleScroll(ScrollEvent e){

        double scrollValue = e.getDeltaY();

        double oldtranslateX = mainimgview.getTranslateX();
        double oldtranslateY = mainimgview.getTranslateY();

        double mainimgScaleUpValue = 1.25;
        double mainimgScaleDownValue = 0.8;
        double rectScaleDownValue = 1/mainimgScaleUpValue;
        double rectScaleUpValue = 1/mainimgScaleDownValue;

        if (scrollValue>0 && rectangle.getWidth()*rectScaleDownValue > 10) {
            goToOrgPos(mainimgview);
            doScale(mainimgview,mainimgScaleUpValue);
            doScale(rectangle,rectScaleDownValue);
            moveObj(mainimgview,oldtranslateX*mainimgScaleUpValue,oldtranslateY*mainimgScaleUpValue);
            zoomslider.setValue(mainimgview.getScaleX());


        }
        else if(scrollValue<0 && rectScaleUpValue*rectangle.getWidth() < smallimgview.getFitWidth()){
            goToOrgPos(mainimgview);
            doScale(mainimgview,mainimgScaleDownValue);
            doScale(rectangle,rectScaleUpValue);
            moveObj(mainimgview,oldtranslateX*mainimgScaleDownValue,oldtranslateY*mainimgScaleDownValue);
            zoomslider.setValue(mainimgview.getScaleX());
        }

        else if (rectScaleUpValue*rectangle.getWidth() >= smallimgview.getFitWidth()){
            goToOrgPos(mainimgview);
            goToOrgPos(rectangle);
            mainimgview.setScaleX(1);
            mainimgview.setScaleY(1);
            rectangle.setWidth(smallimgview.getFitWidth());
            rectangle.setHeight(smallimgview.getFitHeight());
            zoomslider.setValue(mainimgview.getScaleX());
        }

        coordinateGrid.zoomAmount = mainimgview.getScaleX();

    }
    public void handleDrawMode()
    {
        if(!isDrawMode){
            isDrawMode = true;
            canvasGo.setVisible(true);
        }else{
            isDrawMode = false;
            canvasGo.setVisible(false);
            clearCanvas();
        }
    }
    private void freeDrawing()
    {
        gcF.setLineWidth(2);
        gcF.setStroke(Color.rgb(255,255,0));
        gcF.strokeLine(oldX, oldY, lastX, lastY);
        oldX = lastX;
        oldY = lastY;
    }
    private void clearCanvas(){
        //egyelőre kitörli egy egy rajzolás után azt amit rajzoltunk, de rajzolás közben nem tudunk törölni
        gcF.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
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

        coordinateGrid.moveGridPos(event.getSceneX(),event.getSceneY(),mainimgview.getTranslateX(),mainimgview.getTranslateY());

        coordinates.setText("X Coordinate: " + coordinateGrid.posX + "\nY Coordinate: " + coordinateGrid.posY);
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
        int offsetX = 100;
        int offsetY = 100;

        double distanceX = 960 - (offsetX +(((imgview.getFitWidth()/2)*imgview.getScaleX())-Math.abs(newTranslateX)));
        double distanceY = 540 - (offsetY +(((imgview.getFitHeight()/2)*imgview.getScaleY())-Math.abs(newTranslateY)));

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

    private void snappedImgSave(ArrayList<SnappedImage> snapList){
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(snapLog));
            for (SnappedImage img : snapList){
                br.write(img.getId());
                br.write(seperationString);
                br.write(img.getName());
                br.write(seperationString);
                br.write(Double.toString(img.getMinX()));
                br.write(seperationString);
                br.write(Double.toString(img.getMinY()));
                br.write(seperationString);
                br.write(Double.toString(img.getMaxX()));
                br.write(seperationString);
                br.write(Double.toString(img.getMaxY()));
                br.newLine();
            }
            br.close();

        }
        catch (IOException ex){
            System.err.println("IOException at snaplog save");
        }
    }

    private void snappedImgLoad(ArrayList<SnappedImage> snapList){
        try {
            BufferedReader br = new BufferedReader(new FileReader(snapLog));
            String line = br.readLine();
            while (line != null){
                String splittedLine[] = line.split(seperationString);
                int newId = Integer.parseInt(splittedLine[0]);
                double newMinX = Double.parseDouble(splittedLine[2]);
                double newMinY = Double.parseDouble(splittedLine[3]);
                double newMaxX = Double.parseDouble(splittedLine[4]);
                double newMaxY = Double.parseDouble(splittedLine[5]);
                SnappedImage newImage = new SnappedImage(newId,newMinX,newMinY,newMaxX,newMaxY);
                snapList.add(newImage);
                line = br.readLine();
            }
        }
        catch (IOException ex){
            System.err.println("IOException at snaplog load");
        }
        catch (ArrayIndexOutOfBoundsException ex){
            System.err.println("ArrayIndexOutOfBoundsException at snaplog load");
        }
        for (SnappedImage image : snapList){
            if (Integer.parseInt(image.getId())>snappedCounter){
                snappedCounter = Integer.parseInt(image.getId())+1;
            }
        }
    }

    private void getSnapCoords(){
        if (mainimgview.getTranslateX()>0){
            snapMinXCoord = (int) Math.round(screenWidth/2-((screenWidth/2)*(1/mainimgview.getScaleX())+mainimgview.getTranslateX()*(1/mainimgview.getScaleX())));
            snapMaxXCoord = snapMinXCoord + (int) Math.round(screenWidth*(1/mainimgview.getScaleX()));

            //snapMinXCoord = (int) Math.round(960-(((mainimgview.getFitWidth()/2)*mainimgview.getScaleX())-Math.abs(mainimgview.getTranslateX())));
            //snapMaxXCoord = snapMinXCoord + (int) Math.round(screenWidth*(1/mainimgview.getScaleX()));
        }
        else {
            snapMinXCoord = (int) Math.round(screenWidth/2-(screenWidth/2)*(1/mainimgview.getScaleX())+Math.abs(mainimgview.getTranslateX()*(1/mainimgview.getScaleX())));
            snapMaxXCoord = snapMinXCoord + (int) Math.round(screenWidth*(1/mainimgview.getScaleX()));

            //snapMaxXCoord = (int) Math.round(960-(((mainimgview.getFitWidth()/2)*mainimgview.getScaleX())-Math.abs(mainimgview.getTranslateX())));
            //snapMinXCoord = snapMaxXCoord - (int) Math.round(screenWidth*(1/mainimgview.getScaleX()));

        }

        if (mainimgview.getTranslateY()>0){
            snapMinYCoord = (int) Math.round(screenHeight/2-((screenHeight/2)*(1/mainimgview.getScaleY())+mainimgview.getTranslateY()*(1/mainimgview.getScaleY())));
            snapMaxYCoord = snapMinYCoord + (int) Math.round(screenHeight*(1/mainimgview.getScaleY()));

            //snapMinYCoord = (int) Math.round(540-(((mainimgview.getFitHeight()/2)*mainimgview.getScaleY())-Math.abs(mainimgview.getTranslateY())));
            //snapMaxYCoord = snapMinYCoord + (int) Math.round(screenHeight*(1/mainimgview.getScaleY()));
        }
        else{
            snapMinYCoord = (int) Math.round(screenHeight/2-(screenHeight/2)*(1/mainimgview.getScaleY())+Math.abs(mainimgview.getTranslateY()*(1/mainimgview.getScaleY())));
            snapMaxYCoord = snapMinYCoord + (int) Math.round(screenHeight*(1/mainimgview.getScaleY()));

            //snapMaxYCoord = (int) Math.round(540-(((mainimgview.getFitHeight()/2)*mainimgview.getScaleY())-Math.abs(mainimgview.getTranslateY())));
            //snapMinYCoord = snapMaxYCoord - (int) Math.round(screenHeight*(1/mainimgview.getScaleY()));
        }
    }

}
