package MainPackage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

/**
 * Utility class for changing the current scene into another fxml.
 */
class ScreenController {

    ScreenController() {}

    /**
     * Set the scene into the specified fxml.
     *
     * @param scene : Current scene.
     * @param name : Name of the fxml file.
     */
    void setScreen(Scene scene, String name){
        try {
           scene.setRoot(FXMLLoader.load(getClass().getResource(name)));
        }
        catch (IOException ex){
            System.err.println("IOException at scene change!");
        }
    }
}
