package MainPackage;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class IniTools {

    static Ini settings = null;
    private String sectionName;

    IniTools(String sectionName) {
        this.sectionName = sectionName;
    }

    static boolean initializeINIFile(File file){
        try {
            settings = new Ini(file);
            return true;
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return false;
    }

     public String getValue(String key){
        return settings.get(this.sectionName,key);
    }
}
