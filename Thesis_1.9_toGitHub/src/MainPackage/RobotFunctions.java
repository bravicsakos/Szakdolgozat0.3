package MainPackage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static MainPackage.IniTools.*;
import static MainPackage.MainPageController.projectFolder;
import static java.awt.event.KeyEvent.*;

/**
 * Methods and functions using the java.awt.Robot class.
 *
 * Uses a robot to generate key pushes.
 * variable robot : The main robot which makes the key pushes.
 * variable timer : Timer for continuous check of a specific file.
 * variable numberOfPictures : The number of pictures the Panoramic Viewer creates
 *                              when cutting up the original image.
 * variable delay : Delay time for running the key pushes after the program loads
 *                   and not before in miliseconds.
 */
public class RobotFunctions {

    private static Robot robot = null;
    private static Timer timer = new Timer();
    private static int numberOfPictures = -1;
    private static final int delay = 20000;

    /**
     * Resembles a key push.
     *
     * @param keyevent : The key that needs to be pushed.
     */
    private static void keyPush(int keyevent){
        try {
            robot = new Robot();
        }
        catch (AWTException ex){
            ex.printStackTrace();
        }

        robot.keyPress(keyevent);
        robot.keyRelease(keyevent);
    }

    /**
     * Resembles multiple key pushes.
     * Overloads the {@Method keyPush}.
     *
     * @param keyevent : The key to be pushed.
     * @param count : How many times it should be pushed.
     */
    private static void keyPush(int keyevent, int count){
        try {
            robot = new Robot();
        }
        catch (AWTException ex){
            ex.printStackTrace();
        }

        for (int i = 0; i < count; i++) {
            robot.keyPress(keyevent);
            robot.keyRelease(keyevent);
        }

    }

    /**
     * The specific set of key pushes and actions to
     * save images with the specific settings inside
     * Panoramic Viewer.
     */
    public static void panViewerSaver(){
        try{
            robot = new Robot();
        }
        catch (AWTException ex){
            ex.printStackTrace();
        }
        robot.delay(delay);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_E);

        robot.keyRelease(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        keyPush(VK_DOWN, 2);
        keyPush(VK_TAB, 2);
        keyPush(VK_DOWN, 2);
        keyPush(VK_TAB, 4);
        keyPush(VK_8);
        keyPush(VK_0);
        keyPush(VK_TAB, 4);
        keyPush(VK_LEFT);
        keyPush(VK_TAB);
        keyPush(VK_ENTER);
        keyPush(VK_TAB);
        String myString = projectFolder + "\\" +  settings.get("ROBOT_FUNCTIONS","RAW_IMAGE_FOLDER_BASE_NAME");
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        keyPush(VK_TAB, 2);
        keyPush(VK_ENTER);

    }

    /**
     * Checks whether the images are done saving.
     *
     * Destroys the process and stops the checking if it's done.
     * @param process : The process to be destroyed.
     */
    public static void saveProgressChecker(Process process){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (runFileScan()){
                    timer.cancel();
                    process.destroy();
                }
            }
        };
        timer.scheduleAtFixedRate(task,0,10000);
        try {
            process.waitFor();
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

    }

    /**
     * Counts the saved images and compares it to the full number.
     *
     * @return : Whether all the files are saved and in place.
     */
    public static boolean runFileScan(){
        try {
            metaSearch();
            int counter = 0;
            File file = new File(projectFolder + "\\" + settings.get("ROBOT_FUNCTIONS", "RAW_IMAGE_FOLDER_FULL_NAME") + "\\");
            for (File ignored : Objects.requireNonNull(file.listFiles())) {
                counter++;
            }
            if (counter == 0) {
                return false;
            }
            counter--;
            return counter == numberOfPictures;
        }
        catch (NullPointerException ignored){}
        return false;

    }

    /**
     * Reads the meta file created at saving and updates the {@variable numberOfPictures}
     */
    private static void metaSearch(){
        File file = new File(projectFolder  + "\\" + settings.get("ROBOT_FUNCTIONS","RAW_IMAGE_FOLDER_FULL_NAME") + "\\_meta.xml");
        if (!file.exists()){
            return;
        }
        String line;
        String previousLine = "";
        String previous2Line = "";
        numberOfPictures = -1;
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ( (line = br.readLine()) != null){
                previous2Line = previousLine;
                previousLine = line;

            }
            if (!previous2Line.equals("")) {
                numberOfPictures = Integer.parseInt(previous2Line.substring(previous2Line.indexOf('p') + 1, previous2Line.indexOf('>'))) + 1;
            }
            br.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }



}
