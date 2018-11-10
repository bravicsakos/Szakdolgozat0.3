package MainPackage;

import java.io.File;
import java.util.Comparator;


/**
 * Custom comparator for comparing file names.
 *
 * Shorter file names go before longer ones.
 * */
public class CustomComparator implements Comparator<File> {

    @Override
    public int compare(File o1, File o2) {
        if (o1.getName().length()<o2.getName().length()){
            return -1;
        }
        else if (o1.getName().length()>o2.getName().length()){
            return 1;
        }
        else {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
