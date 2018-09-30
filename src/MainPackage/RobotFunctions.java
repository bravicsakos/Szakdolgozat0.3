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
import static MainPackage.PictureChooserController.projectFolder;
import static MainPackage.PictureChooserController.projects;
import static java.awt.event.KeyEvent.*;

public class RobotFunctions {

    private static Robot robot = null;
    private static Timer timer = new Timer();
    private static int numberOfPictures = -1;
    private static final int delay = 20000;

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
        keyPush(VK_DOWN, 3);
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
