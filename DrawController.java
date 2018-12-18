package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.ini4j.Ini;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class DrawController {
    static Canvas canvasGo = new Canvas();
    //Draw variables
    private boolean isDrawMode = false;
    static boolean isRED = true;
    private GraphicsContext gcF;
    private double  lastX,lastY,oldX,oldY;
    private double minX,maxX,maxY,minY;
    private double rectangleHeight=0;
    private double rectangleWidth =0;
    private Rectangle canvasRectangle;
    private File imageFile = new File("Images_Whole Slide_p5111.jpg");
    Dimension screensize= Toolkit.getDefaultToolkit().getScreenSize();
    double screenwidth=screensize.getWidth();
    double screenhight=screensize.getHeight();
    ArrayList<Double> xPositions = new ArrayList<>();
    ArrayList<Double> yPositions = new ArrayList<>();
    //Text txt = new Text();
    //SnapedImg variables
    static Ini settings;
    static {
        try {
            settings = new Ini(new File("Settings.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            gcF = canvasGo.getGraphicsContext2D();
            canvasGo.setWidth(mainPane.getFitWidth());
            canvasGo.setHeight(mainPane.getFitHeight());
            canvasGo.setVisible(true);
            canvasGo.setPickOnBounds(true);
            mainPane.setPickOnBounds(false);
            minX = Double.MAX_VALUE;           //ha MIN_VALUE írsz az a legkisebb reptezenálható számot adja ezért -MAX
            minY = Double.MAX_VALUE;
            maxY = -Double.MAX_VALUE;
            maxX = -Double.MAX_VALUE;
            gcF.setStroke(Color.RED);
            gcF.setLineWidth(2);
            //mainPane.getChildren().add(txt);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
    private void freeDrawing()
    {
        gcF.strokeLine(oldX, oldY, lastX, lastY);
        //System.out.println("oldX: "+ oldX + "   " + "lastX: " + lastX + "\n");
        oldX = lastX;
        oldY = lastY;
    }
    private void moveCanvas(Canvas canvas, double x, double y) {
        canvas.setTranslateX(x);
        canvas.setTranslateY(y);
    };
    public void resetVariables(){
        xPositions.clear();
        yPositions.clear();
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
    }

    public void canvasMousePressed(MouseEvent event) {
        resetVariables();
        this.oldX = event.getX();
        this.oldY = event.getY();
    }
    public void canvasMouseDragged(MouseEvent event) {
        this.lastX = event.getX();
        this.lastY = event.getY();
        xPositions.add(this.lastX);
        yPositions.add(this.lastY);
        //txt.setText(this.lastX + " " + lastY);
        setCoordinates(this.lastX, this.lastY);
        canvasDrawHeightForRectangle(maxY,minY);
        canvasDrawWidthForRectangle(maxX,minX);
        System.out.println("event.getX():   " + event.getX() + "    " + "   " + this.lastX);
        System.out.println("minX:   " + minX + "    " + "maxX:  " + maxX);
        System.out.println("minY():   " + minY + "    " + "maxY:    "+ maxY);
        //System.out.println("Height:   " + rectangleHeight + "    " + "Width:   " + rectangleWidth);*/
        freeDrawing();
    }
    public void canvasMouseRelease(MouseEvent event) throws IOException {
        int tempID = isRED ? Integer.parseInt(settings.get(
                "GENERAL","SNAPPED_INDEX_NEGATIVE")) : Integer.parseInt(settings.get(
                "GENERAL","SNAPPED_INDEX_POSITIVE"));
        SnappedImage snapimg = new SnappedImage(tempID,minX,minY,maxX,maxY,!isRED ? "Positive" : "Negative");
        Rectangle2D rect = new Rectangle2D(
                minX+(screenwidth-1000)/2,minY-100,maxX-minX,maxY-minY);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setViewport(rect);
        parameters.setFill(javafx.scene.paint.Color.rgb(0,0,0));
        WritableImage wimg = mainPane.snapshot(parameters, null);
        File output = FileStructure.makeFile(snapimg);
        ImageIO.write(SwingFXUtils.fromFXImage(wimg,null),"png", output);
        increaseSnappedIndex();
        /*
        xPositions , és yPositions kell kinullázni valahogy mert mindig az előző koordinátákat is felhasználja
        */
        double xPosArray[] = new double[xPositions.size()];
        for (int i = 0; i<xPositions.size();i++){
            xPosArray[i]=xPositions.get(i);
        }
        double yPosArray[] = new double[yPositions.size()];
        for (int i = 0; i<yPositions.size();i++){
            yPosArray[i]=yPositions.get(i);
        }
        Color opaque = new Color(1.0,1.0,1.0,1.0);
        Canvas tempCanvas = new Canvas();
        tempCanvas.setWidth(canvasGo.getWidth());
        tempCanvas.setHeight(canvasGo.getHeight());
        tempCanvas.setVisible(true);
        tempCanvas.setPickOnBounds(true);
        //gcF.setFill(opaque);
        tempCanvas.getGraphicsContext2D().setFill(opaque);
        tempCanvas.getGraphicsContext2D().fillPolygon(xPosArray,yPosArray,xPositions.size());
        mainPane.getChildren().add(tempCanvas);
        File kulonfile = FileStructure.makeCanvasFile(output);
        WritableImage cimg = tempCanvas.snapshot(parameters, null);
        ImageIO.write(SwingFXUtils.fromFXImage(cimg,null),"png",kulonfile);
        mainPane.getChildren().remove(tempCanvas);
        //Set it back to transparent, but it is already set to white first so this doesnt give us the effect we would like to see
        javafx.scene.paint.Color transparent = new javafx.scene.paint.Color(1.0,1.0,1.0,0.0);
//        gcF.setFill(transparent);
        //canvasGo.getGraphicsContext2D().fillPolygon(xPosArray,yPosArray,xPositions.size());
    }
    private static void increaseSnappedIndex() throws IOException {
        if(isRED){
            settings.put("GENERAL","SNAPPED_INDEX_NEGATIVE",
                    String.valueOf(
                            Integer.parseInt(settings.get(
                                    "GENERAL","SNAPPED_INDEX_NEGATIVE"))+1));
        }else{
            settings.put("GENERAL","SNAPPED_INDEX_POSITIVE",
                    String.valueOf(
                            Integer.parseInt(settings.get(
                                    "GENERAL","SNAPPED_INDEX_POSITIVE"))+1));

        }
        settings.store();
    }
    private void setCoordinates(double xCoordinate, double yCoordinate) {
        if (xCoordinate < minX) minX = xCoordinate;
        if (yCoordinate < minY) minY = yCoordinate;
        if (xCoordinate > maxX) maxX = xCoordinate;
        if (yCoordinate > maxY) maxY = yCoordinate;
    }
    private double canvasDrawHeightForRectangle(double localMaxY,double localMinY){
        rectangleHeight = localMaxY-localMinY;
        return rectangleHeight;
    }
    private double canvasDrawWidthForRectangle(double localMaxX,double localMinX){
        rectangleWidth = localMaxX-localMinX;
        return rectangleWidth;
    }

    public void handleColorSwitch(){
        if(gcF.getStroke()== javafx.scene.paint.Color.RED){
            gcF.setStroke(javafx.scene.paint.Color.BLUE);
            isRED = false;
        }
        else {
            gcF.setStroke(Color.RED);
            isRED = true;
        }
    }
}
