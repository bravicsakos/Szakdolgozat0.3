package MainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static MainPackage.Constants.TYPE_MULTIPLE;
import static MainPackage.Constants.TYPE_SINGLE;
import static MainPackage.IniTools.*;

public class ChooserScreenController {
    @FXML
    BorderPane borderPane;
    @FXML
    HBox hBox;
    @FXML
    Button exitButton;

    private static Button singleButton = new Button(settings.get("CHOOSER_SCREEN","SINGLE_BUTTON_TEXT"));
    private static Button multipleButton = new Button(settings.get("CHOOSER_SCREEN","MULTIPLE_BUTTON_TEXT"));

    static int type;

    static final ScreenController screenController = new ScreenController();

    public void initialize(){
        hBox.setSpacing(10);
        exitButton.setText(settings.get("GENERAL","EXIT_BUTTON_TEXT"));
        exitButton.setOnAction(event -> System.exit(0));
        singleButton.setOnAction(event -> {
            type = TYPE_SINGLE;
            screenController.setScreen(singleButton.getScene(), "PictureChooser.fxml");
        });
        multipleButton.setOnAction(event -> {
            type = TYPE_MULTIPLE;
            screenController.setScreen(multipleButton.getScene(),"PictureChooser.fxml");
        });
        hBox.getChildren().addAll(singleButton,multipleButton);
    }
}
