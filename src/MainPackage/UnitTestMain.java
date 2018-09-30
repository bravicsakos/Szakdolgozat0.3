package MainPackage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

class UnitTestMain {
    @Test
    void testRobotFunctions(){
        RobotFunctionsTest.testClipboardSave();
        RobotFunctionsTest.testRobotCreatable();
    }

    @Test
    void testInitIni(){
        assertTrue(IniTools.initializeINIFile(new File("Settings.ini")));
    }
    @Test
    void testComparator(){
        File a = new File("1");
        File b = new File("10");
        File c = new File("2");
        ArrayList<File> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.sort(new CustomComparator());
        assertTrue(list.get(1).getName().equals("2"));
    }
    @Test
    void testPictureRenderer(){

        try {
            PictureRendererTest.testImageRemover();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
