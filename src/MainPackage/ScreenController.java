package MainPackage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;

public class ScreenController {

    public ScreenController() {
    }

    public void setScreen(Scene scene, String name){
        try {
           scene.setRoot(FXMLLoader.load(getClass().getResource(name)));
        }
        catch (IOException ex){
            System.err.println("IOException at scene change!");
        }
    }
}
