package MainPackage;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

/**
 * Ini file handling class
 *
 * @variable settings : Variable for the settings.ini file.
 * @variable sectionName : Variable for specific sections of the settings file.
 * */
class IniTools {

    static Ini settings = null;

    private String sectionName;

    /**
     * Basic constructor for specific section of ini file
     *
     * @param sectionName : Name of the INI section.
     * */
    IniTools(String sectionName) {
        this.sectionName = sectionName;
    }

    /**
     * Load the specified ini files into settings
     *
     * @param file : INI file to be loaded.
     * */
    static void initializeINIFile(File file){
        try {
            settings = new Ini(file);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Get value of specified key of the object section
     *
     * @param key : Key inside the specific section.
     * @return : Value of the key.
     * */
    String getValue(String key){
        return settings.get(this.sectionName,key);
    }
}
