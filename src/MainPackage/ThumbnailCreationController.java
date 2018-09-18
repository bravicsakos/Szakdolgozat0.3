package MainPackage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static MainPackage.ChooserScreenController.screenController;

public class ThumbnailCreationController {

    @FXML
    StackPane stackPane;
    @FXML
    VBox vBox;
    @FXML
    Text text;

    private static TextFlow progressTab = new TextFlow();
    private static Text progressText = new Text();
    private static Text pleaseWaitText = new Text();
    private static Text stopperText = new Text();

    private static long orgTime = (long) 0;
    private static Time elapsedTime = new Time(0);

    private static Timeline waitAnimation = new Timeline();
    private static Timeline stopperAnimation = new Timeline();

    private static File imageFolder = new File("Images_Whole Slide.jpg_Files");
    static File mrxsFile = new File("Images_Whole Slide.jpg");
    static int imageListSize = 0;

    static HashMap<Integer, File> imageFiles = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles1024X1024 = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles512X512 = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles256X256 = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles128X128 = new HashMap<>();

    private final static double imageHeight = 2048;

    public void initialize(){
        vBox.setSpacing(20);
        vBox.setVisible(false);
        vBox.setDisable(true);

        makeProgressTab();
        stackPane.getChildren().add(progressTab);
        progressTab.toBack();

        fontMaker(text);

        Task animStarter = new Task<Void>() {
            @Override
            protected Void call() {
                progressTab.setVisible(true);
                waitAnimation.play();
                orgTime = System.currentTimeMillis();
                stopperAnimation.play();
                return null;
            }
        };
        Task reScaler = new Task<Void>() {
            @Override
            public Void call() {
                int imageID = thumbnailCounter();
                createThumbnails(imageID);
                waitAnimation.stop();
                stopperAnimation.stop();
                vBox.setVisible(true);
                vBox.setDisable(false);
                return null;
            }
        };
        new Thread(animStarter).start();
        initImageProcessing(imageFolder);
        new Thread(reScaler).start();
    }

    private static void makeProgressTab(){
        stopperText.setFill(Color.BLACK);
        progressText.setFill(Color.BLACK);
        pleaseWaitText.setFill(Color.BLACK);
        pleaseWaitText.setText("Image rescaling in progress, please wait.");
        progressTab.getChildren().add(pleaseWaitText);
        progressTab.getChildren().add(progressText);
        progressTab.getChildren().add(stopperText);

        EventHandler<ActionEvent> update = event -> {
            switch (pleaseWaitText.getText()) {
                case "Image rescaling in progress, please wait.":
                    pleaseWaitText.setText("Image rescaling in progress, please wait..");
                    break;
                case "Image rescaling in progress, please wait..":
                    pleaseWaitText.setText("Image rescaling in progress, please wait...");
                    break;
                case "Image rescaling in progress, please wait...":
                    pleaseWaitText.setText("Image rescaling in progress, please wait.");
                    break;
            }
        };

        EventHandler<ActionEvent> update2 = event -> {
            elapsedTime.setTime(System.currentTimeMillis() - orgTime);
            elapsedTime.setHours(0);
            stopperText.setText("\nTime elapsed: " + elapsedTime.toString() + "s");
        };

        fontMakerAll(pleaseWaitText,progressText,stopperText);

        waitAnimation.setCycleCount(Timeline.INDEFINITE);
        waitAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(500), update));

        stopperAnimation.setCycleCount(Timeline.INDEFINITE);
        stopperAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(84), update2));

    }

    private static void fontMaker(Text text){
        Font newFont = new Font("Arial", 20);
        text.setFont(newFont);
    }

    private static void fontMakerAll(Text... texts){
        Font newFont = new Font("Serif", 30);
        for (Text text: texts) {
            text.setFont(newFont);
        }
    }

    private static int thumbnailCounter(){
        int imageID = 0;
        File thumb = new File("Thumbnails_1024X1024\\Thumbnail_" + imageID + ".jpg");
        while (thumb.exists()){
            imageID++;
            thumb = new File("Thumbnails_1024X1024\\Thumbnail_" + imageID + ".jpg");
        }
        return --imageID>0?imageID:0;
    }

    private static void initImageProcessing(File imageFolder){
        File firstImage;
        if (!imageFolder.exists()){
            System.err.println("File doesn't exist!");
            return;
        }
        int i = 0;
        File[] files = Objects.requireNonNull(imageFolder.listFiles());
        Arrays.asList(files).sort(new CustomComparator());
        for (File f: files) {
            if (!f.getName().equals("_meta.xml")){
                imageFiles.put(i,f);
                i++;
            }
        }
        firstImage = imageFiles.get(0);

        int subStrEnd = firstImage.getName().lastIndexOf('.');

        String imageName = firstImage.getName().substring(0, subStrEnd - 1);
        String imageFormat = firstImage.getName().substring(subStrEnd);

        ImageV2.setGenericName(imageName);
        ImageV2.setFormat(imageFormat);

        imageListSize = imageFiles.size();
    }

    private static void createThumbnails(int fromID) {
        File pict = imageFiles.get(fromID);
        File thumbFolder_1024X1024 = new File("Thumbnails_1024X1024");
        File thumbFolder_512X512 = new File("Thumbnails_512X512");
        File thumbFolder_256X256 = new File("Thumbnails_256X256");
        File thumbFolder_128X128 = new File("Thumbnails_128X128");
        if (    (thumbFolder_1024X1024.mkdir() &&
                thumbFolder_512X512.mkdir() &&
                thumbFolder_256X256.mkdir() &&
                thumbFolder_128X128.mkdir()) ||
                (thumbFolder_128X128.exists() &&
                        thumbFolder_256X256.exists() &&
                        thumbFolder_512X512.exists() &&
                        thumbFolder_1024X1024.exists())) {
            while (pict.exists()) {
                progressText.setText("\nImage " + (fromID + 1) + "/" + imageListSize + " is rescaled!");
                try {
                    Image img = new Image(pict.toURI().toURL().toString());
                    ThumbnailCreater.makeThumbnail(img, (int) imageHeight, "Thumbnail_" + fromID + ".jpg");
                } catch (MalformedURLException ex) {
                    System.err.println("Malformed URL at creating thumbnail! PictureRenderer/ThumbnailCreater");
                }
                fromID++;
                if (fromID >= imageListSize) {
                    break;
                }
                pict = imageFiles.get(fromID);
            }

            int i = 0;
            File[] files1 = thumbFolder_1024X1024.listFiles();
            File[] files2 = thumbFolder_512X512.listFiles();
            File[] files3 = thumbFolder_256X256.listFiles();
            File[] files4 = thumbFolder_128X128.listFiles();
            assert files1 != null;
            Arrays.asList(files1).sort(new CustomComparator());
            assert files2 != null;
            Arrays.asList(files2).sort(new CustomComparator());
            assert files3 != null;
            Arrays.asList(files3).sort(new CustomComparator());
            assert files4 != null;
            Arrays.asList(files4).sort(new CustomComparator());
            for (File f : files1) {
                thumbnailFiles1024X1024.put(i, f);
                i++;
            }
            i = 0;
            for (File f : files2) {
                thumbnailFiles512X512.put(i, f);
                i++;
            }
            i = 0;
            for (File f : files3) {
                thumbnailFiles256X256.put(i, f);
                i++;
            }
            i = 0;
            for (File f : files4) {
                thumbnailFiles128X128.put(i, f);
                i++;
            }
        }

    }

    public void handleButton(){
        screenController.setScreen(progressTab.getScene(),"LowQualityRender.fxml");
    }

}
