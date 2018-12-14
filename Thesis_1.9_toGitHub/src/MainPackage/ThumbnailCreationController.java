package MainPackage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static MainPackage.IniTools.settings;
import static MainPackage.MainPageController.*;


/**
 * Class for creating thumbnails from the cut images.
 *
 * variable orgTime : Time when the creation is started.
 * variable elapsedTime : Time since the creation is started.
 *
 * variable waitAnimation : Continuous refresh of the created number of created images.
 * variable stopperAnimation : Continuous refresh of the elapsed time.
 *
 * variable imageFolder : Folder where the raw images are located.
 * variable imageListSize : Length of the list of raw images.
 *
 * variable imageFiles,
 *           thumbnailFiles1024X1024,
 *           thumbnailFiles512X512,
 *           thumbnailFiles256X256,
 *           thumbnailFiles128X128 : Maps the image files to an index.
 *
 * variable imageHeight : Height of the raw images ( = width )
 *
 * variable counter : The index of the current image to be rescaled.
 */
public class ThumbnailCreationController {

    private static long orgTime = (long) 0;
    private static Time elapsedTime = new Time(0);

    private static Timeline waitAnimation = new Timeline();
    private static Timeline stopperAnimation = new Timeline();

    private static File imageFolder = new File(projectFolder + "\\" + settings.get("ROBOT_FUNCTIONS","RAW_IMAGE_FOLDER_FULL_NAME") + "\\");
    static int imageListSize = 0;

    static HashMap<Integer, File> imageFiles = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles1024X1024 = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles512X512 = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles256X256 = new HashMap<>();
    static HashMap<Integer, File> thumbnailFiles128X128 = new HashMap<>();

    private final static double imageHeight = Integer.parseInt(sectionThumbnail.getValue("IMAGE_HEIGHT"));

    private static int counter = 0;

    private static Vector<Integer> lastImageIDs;
    private static boolean isGrid = false;

    private static ArrayList<Thread> rescaleThreads;

    private static final ExecutorService executor = Executors.newFixedThreadPool(40);
    /**
     * Start the rescaling process.
     *
     * Shows the info about the rescaling, starts the animations
     * and the rescaling procedure.
     * Shows a button when completed.
     */
    public static void run(){
        if (new File(projectFolder + "\\Grid").exists()){
            isGrid = true;
            imageFolder = new File(projectFolder + "\\Grid");
        }
        MainPageController.setEnabled(false);
        makeProgressTab();
        main.setCenter(thumbnailPane);
        main.setLeft(null);
        main.setRight(null);
        Task animStarter = new Task<Void>() {
            @Override
            protected Void call() {
                waitAnimation.play();
                orgTime = System.currentTimeMillis();
                stopperAnimation.play();
                return null;
            }
        };
        Task reScaler = new Task<Void>() {
            @Override
            public Void call() {
                lastImageIDs = thumbnailCounter();
                createThumbnails();
                waitAnimation.stop();
                stopperAnimation.stop();
                finishedButton.setVisible(true);
                finishedButton.setDisable(false);
                return null;
            }
        };
        new Thread(animStarter).start();
        initImageProcessing(imageFolder);
        new Thread(reScaler).start();
    }

    /**
     * Creates the animations for the info tab.
     */
    private static void makeProgressTab(){

        EventHandler<ActionEvent> update = event -> {
            switch (counter) {
                case 0:
                    pleaseWaitText.setText(sectionThumbnail.getValue("PLEASE_WAIT_TEXT_BASE") + "..");
                    counter = 1;
                    break;
                case 1:
                    pleaseWaitText.setText(sectionThumbnail.getValue("PLEASE_WAIT_TEXT_BASE") + "...");
                    counter = 2;
                    break;
                case 2:
                    pleaseWaitText.setText(sectionThumbnail.getValue("PLEASE_WAIT_TEXT_BASE") + ".");
                    counter = 0;
                    break;
            }
        };

        EventHandler<ActionEvent> update2 = event -> {
            elapsedTime.setTime(System.currentTimeMillis() - orgTime);
            elapsedTime.setHours(0);
            stopperText.setText("\nTime elapsed: " + elapsedTime.toString() + "s");
        };

        waitAnimation.setCycleCount(Timeline.INDEFINITE);
        waitAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(500), update));

        stopperAnimation.setCycleCount(Timeline.INDEFINITE);
        stopperAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(84), update2));

    }

    /**
     * Counts the existing thumbnails.
     *
     * Creates a vector where:
     * Value 0 : 1024X1024 thumbnails last created thumbnail ID
     * Value 1 : 512X512 thumbnails last created thumbnail ID
     * Value 2 : 256X256 thumbnails last created thumbnail ID
     * Value 3 : 128X128 thumbnails last created thumbnail ID
     *
     * @return : The created vector.
     */
    private static Vector<Integer> thumbnailCounter(){
        Vector<Integer> lastImages = new Vector<>();
        String format;
        if (isGrid){
            format = sectionThumbnail.getValue("THUMBNAIL_FORMAT_PNG");
        }
        else {
            format = sectionThumbnail.getValue("THUMBNAIL_FORMAT_JPG");
        }
        lastImages.add(countThumbnails(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_1024X1024_NAME") + "\\" +
                sectionThumbnail.getValue("THUMBNAIL_NAME"),format));
        lastImages.add(countThumbnails(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_512X512_NAME") + "\\" +
                sectionThumbnail.getValue("THUMBNAIL_NAME"),format));
        lastImages.add(countThumbnails(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_256X256_NAME") + "\\" +
                sectionThumbnail.getValue("THUMBNAIL_NAME"),format));
        lastImages.add(countThumbnails(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_128X128_NAME") + "\\" +
                sectionThumbnail.getValue("THUMBNAIL_NAME"),format));
        return lastImages;
    }

    /**
     * Counts files in a specific location with an increasing ID
     *
     * @param beforeID : The file path and name before the ID
     * @param afterID : Name and/or extension
     * @return : Number of files matching the before and after ID convention
     */
    private static int countThumbnails(String beforeID, String afterID){
        int imageID = 1;
        File thumb = new File(beforeID + imageID + afterID);
        while (thumb.exists()){
            imageID++;
            thumb = new File(beforeID + imageID + afterID);
        }
        return --imageID > 0 ? imageID : 0;
    }

    /**
     * Makes the {@variable imageFiles} map and the {@variable imageListSize}
     *
     * @param imageFolder : Folder for the raw images.
     */
    private static void initImageProcessing(File imageFolder){
        if (!imageFolder.exists()){
            System.err.println("File doesn't exist!");
            return;
        }
        int i = 0;
        File[] files = Objects.requireNonNull(imageFolder.listFiles());
        Arrays.sort(files, new CustomComparator());
        for (File f: files) {
            if (!f.getName().equals("_meta.xml")){
                imageFiles.put(i,f);
                i++;
            }
        }
        imageListSize = imageFiles.size();
    }

    /**
     * The main method for rescaling the images.
     *
     * Creates the folders for the thumbnails if needed,
     * rescales the images, updates the thumbnail hash maps.
     */
    private static void createThumbnails() {
        rescaleThreads = createThreads(100);
        int ID = min(lastImageIDs);
        File pict = imageFiles.get(ID);
        File thumbFolder_1024X1024 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_1024X1024_NAME"));
        File thumbFolder_512X512 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_512X512_NAME"));
        File thumbFolder_256X256 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_256X256_NAME"));
        File thumbFolder_128X128 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_128X128_NAME"));
        thumbFolder_1024X1024.mkdir();
        thumbFolder_512X512.mkdir();
        thumbFolder_256X256.mkdir();
        thumbFolder_128X128.mkdir();
        if (    thumbFolder_128X128.exists() &&
                thumbFolder_256X256.exists() &&
                thumbFolder_512X512.exists() &&
                thumbFolder_1024X1024.exists()) {
            while (pict.exists()) {
               while (Thread.activeCount() > 90);
                // TODO : Fixed thread count image creation
                progressText.setText("\nImage " + (ID + 1) + "/" + imageListSize + " is rescaled!");
                int finalID = ID;
                //Platform.runLater(() -> progressBar.setProgress((double) (finalID + 1) / imageListSize));
                try {
                    Image img = new Image(pict.toURI().toURL().toString());
                    if (isGrid) {
                        makeThumbnail(img, (int) imageHeight, sectionThumbnail.getValue("THUMBNAIL_NAME") + ID +
                                sectionThumbnail.getValue("THUMBNAIL_FORMAT_PNG"), ID);
                    }
                    else {
                        makeThumbnail(img, (int) imageHeight, sectionThumbnail.getValue("THUMBNAIL_NAME") + ID +
                                sectionThumbnail.getValue("THUMBNAIL_FORMAT_JPG"), ID);
                    }
                } catch (MalformedURLException ex) {
                    System.err.println("Malformed URL at creating thumbnail!");
                }
                ID++;
                if (ID >= imageListSize) {
                    break;
                }
                pict = imageFiles.get(ID);
            }

            int i = 0;
            File[] files1 = thumbFolder_1024X1024.listFiles();
            File[] files2 = thumbFolder_512X512.listFiles();
            File[] files3 = thumbFolder_256X256.listFiles();
            File[] files4 = thumbFolder_128X128.listFiles();
            assert files1 != null;
            Arrays.sort(files1, new CustomComparator());
            assert files2 != null;
            Arrays.sort(files2, new CustomComparator());
            assert files3 != null;
            Arrays.sort(files3, new CustomComparator());
            assert files4 != null;
            Arrays.sort(files4, new CustomComparator());
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
        MainPageController.setEnabled(true);

    }

    /**
     * Rescales the given picture into for progressively smaller ones and saves them.
     *
     * @param picture : Image to be rescaled.
     * @param orgSize : Size of original image.
     * @param fileName : Name of the file to be created with the rescaled image.
     */
    private static void makeThumbnail(Image picture, int orgSize, String fileName, int id){
        // BUG : after finished button appears some processes are still running
        // TODO : check for last thread activity
        BufferedImage fullImg = SwingFXUtils.fromFXImage(picture,null);
        String format = isGrid ? "png" : "jpg";

        Runnable imgDownscale_1024X1024 = () -> {
            BufferedImage thumbnail_1024X1024 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/2);
            File thumbFile_1024X1024 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_1024X1024_NAME") + "\\" + fileName);
            try{
                ImageIO.write(thumbnail_1024X1024,format,thumbFile_1024X1024);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };
        Runnable imgDownscale_512X512 = () -> {
            BufferedImage thumbnail_512X512 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/4);

            File thumbFile_512X512 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_512X512_NAME") + "\\" + fileName);

            try{
                ImageIO.write(thumbnail_512X512,format,thumbFile_512X512);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };
        Runnable imgDownscale_256X256 = () -> {
            BufferedImage thumbnail_256X256 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/8);

            File thumbFile_256X256 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_256X256_NAME") + "\\" + fileName);
            try{
                ImageIO.write(thumbnail_256X256,format,thumbFile_256X256);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };
        Runnable imgDownscale_128X128 = () -> {
            BufferedImage thumbnail_128X128 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/16);
            File thumbFile_128X128 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_128X128_NAME") + "\\" + fileName);

            try{
                ImageIO.write(thumbnail_128X128,format,thumbFile_128X128);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };

        if (id > lastImageIDs.get(0)) {
            //executor.submit(imgDownscale_1024X1024);
            //runOnAvailableThread(imgDownscale_1024X1024);
            new Thread(imgDownscale_1024X1024).start();
        }
        if (id > lastImageIDs.get(1)) {
            //executor.submit(imgDownscale_512X512);
            //runOnAvailableThread(imgDownscale_512X512);
            new Thread(imgDownscale_512X512).start();
        }
        if (id > lastImageIDs.get(2)) {
            //executor.submit(imgDownscale_256X256);
            //runOnAvailableThread(imgDownscale_256X256);
            new Thread(imgDownscale_256X256).start();
        }
        if (id > lastImageIDs.get(3)) {
            //executor.submit(imgDownscale_128X128);
            //runOnAvailableThread(imgDownscale_128X128);
            new Thread(imgDownscale_128X128).start();
        }



    }


    /**
     * Searches the smallest number in an Integer vector.
     *
     * @param vector : The given vector to search
     * @return : The smallest number
     */
    private static int min(Vector<Integer> vector){
        int min = vector.get(0);
        for (Integer i: vector) {
            if (i < min){
                min = i;
            }
        }
        return min;
    }

    private static ArrayList<Thread> createThreads(int count){
        ArrayList<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            threadList.add(new Thread());
        }
        return threadList;
    }

    private static void runOnAvailableThread(Runnable task){
        boolean found = false;
        while (!found){
            for (Thread t: rescaleThreads) {
                if (!t.isAlive()){
                    t = new Thread(task);
                    t.start();
                    found = true;
                    break;
                }
            }
        }
    }


}
