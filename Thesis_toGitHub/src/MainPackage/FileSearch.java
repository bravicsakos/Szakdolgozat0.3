package MainPackage;

import java.io.File;
import java.util.Objects;

/**
 * Class for searching a specific file on computer.
 *
 * @variable fileName: The name of the file to search for.
 * @variable result : The file that was found with the name given.
 * */
class FileSearch {

    static String fileName;
    static File result;

    /**
     * The search function where a base directory must be given.
     *
     * @param directory : base directory to search in.
     * If result is found then it's stored in {@variable result}.
     * */
    static void search(File directory){
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
