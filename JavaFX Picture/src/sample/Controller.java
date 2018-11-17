package sample;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.net.MalformedURLException;

public class Controller {
    @FXML
    GridPane mainPane;

    private File imageFile = new File("Images_Whole Slide_p5111.jpg");


    public void initialize() {
        try {
            Image img = new Image(imageFile.toURI().toURL().toString());
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(1000);
            imageView.setPreserveRatio(true);
            mainPane.getChildren().add(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
