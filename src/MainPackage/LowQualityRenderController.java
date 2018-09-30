package MainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.net.MalformedURLException;

import static MainPackage.ChooserScreenController.screenController;
import static MainPackage.IniTools.*;
import static MainPackage.ThumbnailCreationController.mrxsFile;

public class LowQualityRenderController {

    @FXML
    ImageView mrxsView;
    @FXML
    Button gridViewButton;
    @FXML
    Button exitButton;

    private double orgSceneX, orgSceneY;
    private static double orgTranslateX, orgTranslateY;

    public void initialize(){
        exitButton.setText(settings.get("GENERAL","EXIT_BUTTON_TEXT"));
        exitButton.setOnAction(event -> System.exit(0));
        gridViewButton.setText(settings.get("LOW_QUALITY_RENDER","GRID_VIEW_BUTTON_TEXT"));
        File temp = mrxsFile;
        try{
            Image mrxsImage = new Image(temp.toURI().toURL().toString());
            mrxsView.setImage(mrxsImage);
            mrxsView.setRotate(270);
        }
        catch (MalformedURLException ex){
            System.err.println("Malformed URL at addMrxs");
        }
    }

    public void handleMousePress(MouseEvent event){
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = mrxsView.getTranslateX();
            orgTranslateY = mrxsView.getTranslateY();
    }

    public void handleMouseDrag(MouseEvent event){
            double offsetX = event.getSceneX() - orgSceneX;
            double offsetY = event.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            mrxsView.setTranslateX(newTranslateX);
            mrxsView.setTranslateY(newTranslateY);
    }

    public void handleGridViewButton(){
        screenController.setScreen(gridViewButton.getScene(),"GridRender.fxml");
    }

}
