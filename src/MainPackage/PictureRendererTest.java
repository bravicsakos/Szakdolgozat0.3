package MainPackage;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class PictureRendererTest {
    public static void testImageRemover() throws MalformedURLException {
        GridRenderController.grid = new GridPane();
        GridPane grid = GridRenderController.grid;
        ArrayList<Integer> templist = new ArrayList<>();
        Vector<Integer> inputList = new Vector<>();
        Random rnd = new Random();
        int randomNumber = rnd.nextInt(9)+1;
        for (int i = 0; i < 10; i++) {
            ImageV2 img = new ImageV2(new File("C:\\Users\\qwerty\\IdeaProjects\\szakdogaMain\\Images_Whole Slide.jpg").toURI().toURL().toString(),i,0,0);
            ImageView imgview = new ImageView(img);
            grid.getChildren().add(imgview);
            templist.add(i);
        }
        Collections.shuffle(templist);
        for (int i = 0; i < randomNumber; i++) {
            inputList.add(templist.get(i));
        }
        PictureRenderer.imageRemover(inputList);
        assertEquals(10-randomNumber,grid.getChildren().size());
        for (int i = 0; i < 10-randomNumber; i++) {
            for (int j = 0; j < randomNumber; j++) {
                assertTrue(((ImageV2)((ImageView)grid.getChildren().get(i)).getImage()).getId() != inputList.get(j));
            }
            for (int j = randomNumber; j < 10; j++) {
                assertTrue(((ImageV2)((ImageView)grid.getChildren().get(i)).getImage()).getId() != inputList.get(j));
            }
        }

    }
}
