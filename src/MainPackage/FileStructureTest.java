package MainPackage;
import static org.junit.jupiter.api.Assertions.*;

public class FileStructureTest {
    void testAnalyze(){
        FileStructure.analyseSequenceID("123456789");
        assertEquals("123",FileStructure.folderOneName);
        assertEquals("456",FileStructure.folderTwoName);
    }
    void testPathFormatter(){
        String str = FileStructure.pathFormatter("string1","string2","string3","string4");
        assertEquals("string1\\string2\\string3\\string4",str);
    }
}
