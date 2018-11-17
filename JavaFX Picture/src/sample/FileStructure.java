package sample;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


/**
 * Class for making the database with the saved pictures
 *
 * @variable mainFolder : Base folder to save images to.
 * @variable folderOneName, folderTwoName : Base name for inside folders.
 * @variable snapExtension,
 *           snapLogName,
 *           snapLogExtension,
 *           folderLogName,
 *           folderLogExtension : Names and extensions for the created files
 * @variable warningMsg : Warning messages for failed file creation
 * */
class FileStructure {

    private static final String mainFolder = "Snapped Images";

    private static String folderOneName = "000";
    private static String folderTwoName = "000";

    private static final String snapExtension = ".png";
    private static final String snapLogName = "Log_";
    private static final String snapLogExtension = ".txt";
    private static final String folderLogName = "FolderLog_";
    private static final String folderLogExtension = snapLogExtension;

    private static final String warningMsg = "Couldn't create file at locaton: ";

    /**
     * Set folder names for given ID
     *
     * @param sequenceID : 9 digit sequence ID
     * */
    private static void analyseSequenceID(String sequenceID){
        folderOneName = sequenceID.substring(0,3);
        folderTwoName = sequenceID.substring(3,6);
    }

    /**
     * Make the directories containing the files and their logs
     *
     * Pops error message and return if file could't be created.
     * */
    private static void makeDirectory(){
        File mainFile = new File(mainFolder);
        if (!mainFile.exists()){
            if (!(new File(mainFolder).mkdir())){
                JOptionPane.showMessageDialog(null,warningMsg + mainFile.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        File folderOne = new File(pathFormatter(mainFolder,folderOneName));
        if (folderOne.exists()){
            File folderTwo = new File(pathFormatter(mainFolder,folderOneName,folderTwoName));
            if (folderTwo.exists()){
                return;
            }
            if (!folderTwo.mkdir()){
                JOptionPane.showMessageDialog(null,warningMsg + folderTwo.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            File snapLog = new File(pathFormatter(mainFolder,folderOneName,folderTwoName,snapLogName + folderOneName + folderTwoName + snapLogExtension));
            try{
                if (!snapLog.createNewFile()){
                    JOptionPane.showMessageDialog(null,warningMsg + snapLog.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                initializeFileLogger(snapLog);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
            return;
        }
        if (!folderOne.mkdir()){
            JOptionPane.showMessageDialog(null,warningMsg + folderOne.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        File folderTwo = new File(pathFormatter(mainFolder,folderOneName,folderTwoName));
        if (!folderTwo.mkdir()){
            JOptionPane.showMessageDialog(null,warningMsg + folderTwo.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        File snapLog = new File(pathFormatter(mainFolder,folderOneName,folderTwoName,snapLogName + folderOneName + folderTwoName + snapLogExtension));
        try{
            if (!snapLog.createNewFile()){
                JOptionPane.showMessageDialog(null,warningMsg + snapLog.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            initializeFileLogger(snapLog);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        File folderLog = new File(pathFormatter(mainFolder,folderOneName,folderLogName + folderOneName + folderLogExtension));
        try{
            if (!folderLog.createNewFile()){
                JOptionPane.showMessageDialog(null,warningMsg + folderLog.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Main method to save the given image
     *
     * @param snappedImage : image to be saved.
     * @return : the file where the image
     *           is supposed to be saved.
     * */
    static File makeFile(SnappedImage snappedImage){
        String sequenceID = snappedImage.getId();
        analyseSequenceID(sequenceID);
        makeDirectory();
        return new File(pathFormatter(mainFolder,folderOneName,folderTwoName,sequenceID + snapExtension));
    }

    /**
     * Utility method for creating path to files
     *
     * @param strings : strings ti be formatted.
     * @return : the formatted final string.
     * */
    private static String pathFormatter(String... strings){
        StringBuilder finalString = new StringBuilder(strings[0]);
        boolean isFirst = true;
        for (String s: strings) {
            finalString.append("\\");
            if (!isFirst){
                finalString.append(s);
            }
            isFirst = false;

        }
        return finalString.toString();
    }

    /**
     * Method called to create first line of file logger
     *
     * Adds basic header.
     * @param fileLog : the specific file logger to init.
     * */
    private static void initializeFileLogger(File fileLog){
        String firstLine = "ID       \tMinX\tMinY\tMaxX\tMaxY\tTime created";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileLog));
            bw.write(firstLine);
            bw.newLine();
            bw.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Log the given image to the file logger
     *
     * @param snappedImage : the image to insert into file logger.
     * */
    static void fileLogger(SnappedImage snappedImage){
        File file = new File(pathFormatter(mainFolder,folderOneName,folderTwoName,snapLogName + folderOneName + folderTwoName + snapLogExtension));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(snappedImage.getId());
        stringBuilder.append("\t");
        stringBuilder.append(snappedImage.getMinX());
        stringBuilder.append("\t");
        stringBuilder.append(snappedImage.getMinY());
        stringBuilder.append("\t");
        stringBuilder.append(snappedImage.getMaxX());
        stringBuilder.append("\t");
        stringBuilder.append(snappedImage.getMaxY());
        stringBuilder.append("\t");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss");
        stringBuilder.append(sdf.format(cal.getTime()));

        try {
            Files.write(Paths.get(file.getAbsolutePath()), stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(file.getAbsolutePath()), System.getProperty("line.separator").getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log folder info into folder logger
     * */
    static void folderLogger(){
        File folderOne = new File(pathFormatter(mainFolder,folderOneName));
        int fileCount;
        ArrayList<Integer> fileCountList = new ArrayList<>();
        ArrayList<String> lastDates = new ArrayList<>();
        int folderTwoCount = Objects.requireNonNull(folderOne.listFiles()).length;
        int i = 1;
        for (File folderTwo: Objects.requireNonNull(folderOne.listFiles())) {
            if (i == folderTwoCount){
                break;
            }
            fileCount = Objects.requireNonNull(folderTwo.listFiles()).length;
            fileCountList.add(fileCount - 1);
            File logger = new File(pathFormatter(mainFolder, folderOneName, folderTwo.getName(), snapLogName + folderOneName + folderTwo.getName() + snapLogExtension));
            try {
                BufferedReader br = new BufferedReader(new FileReader(logger));
                String line;
                String lastLine = "";
                while ((line = br.readLine()) != null) {
                    lastLine = line;
                }
                lastDates.add(lastLine.substring(lastLine.length() - 21));
            } catch (IOException ex) {

                ex.printStackTrace();
            }

            i++;
        }
        File folderLog = new File(pathFormatter(mainFolder,folderOneName,folderLogName + folderOneName + folderLogExtension));
        String firstLine = "Folder\tNumber of files\tLast Created";
        int index = 0;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(folderLog));
            bw.write(firstLine);
            i = 1;
            for (File folderTwo: Objects.requireNonNull(folderOne.listFiles())) {
                if (i == folderTwoCount){
                    break;
                }
                String line = folderTwo.getName() +
                        "\t" +
                        fileCountList.get(index) +
                        "\t" +
                        lastDates.get(index);
                index++;
                bw.newLine();
                bw.write(line);
                i++;
            }
            bw.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }


}