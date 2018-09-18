package MainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import static MainPackage.Constants.TYPE_MULTIPLE;
import static MainPackage.Constants.TYPE_SINGLE;

public class ChooserScreenController {
    @FXML
    StackPane stackPane;
    @FXML
    HBox hBox;
    @FXML
    Button singleButton;
    @FXML
    Button multipleButton;

    static int type;

    static final ScreenController screenController = new ScreenController();

    public void initialize(){
        hBox.setSpacing(10);
    }

    public void handleSingleButton(){
        type = TYPE_SINGLE;
        screenController.setScreen(singleButton.getScene(),"PictureChooser.fxml");
    }

    public void handleMultipleButton(){
        type = TYPE_MULTIPLE;
        screenController.setScreen(multipleButton.getScene(),"PictureChooser.fxml");
    }

}
