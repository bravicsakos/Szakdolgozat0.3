package MainPackage;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class RobotFunctionsTest {
    static void testRobotCreatable(){
        Robot r = null;
        try{
            r = new Robot();
        }
        catch (AWTException ignored){ }
        assertNotNull(r);
    }
    static void testClipboardSave(){
        try {
            String myString = "Testing String";
            StringSelection stringSelection = new StringSelection(myString);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            assertEquals("Testing String", clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor));
        }
        catch (IOException | UnsupportedFlavorException ignored){
            assertTrue(false);
        }

    }


}
