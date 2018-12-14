package MainPackage;

import java.io.File;
import java.util.Objects;

/**
 * Class for searching a specific file on computer.
 * */
class FileSearch {

    /**
     * Name of the file to be searched for.
     */
    static String fileName;

    /**
     * File with path to the found file.
     */
    static File result;

    /**
     * The search function where a base directory must be given.
     *
     *  <p>
     *      If result is found then it's stored in result.
     *  </p>
     * @param directory : base directory to search in.
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
