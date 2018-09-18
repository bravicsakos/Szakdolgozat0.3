package MainPackage;

import java.io.File;
import java.util.Comparator;

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
