package MainPackage;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FileSearch {

    static String fileName;
    static File result;

    public static void search(File directory){
        try {
            if (directory.isDirectory()) {
                if (directory.canRead()) {
                    for (File file : Objects.requireNonNull(directory.listFiles())) {
                        if (file.isDirectory()) {
                            search(file);
                        } else if (file.getName().equals(fileName)) {
                            result = new File(file.getPath());
                            return;
                        }
                    }
                }
            }
        }
        catch (NullPointerException ignored){}
    }


}
