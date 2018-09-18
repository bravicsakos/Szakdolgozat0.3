package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("ChooserScreen.fxml"));
        Scene scene = new Scene(root, 1000, 820);
        primaryStage.setTitle("SzakdogaProject0.8");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);

        scene.getRoot().requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
