import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

public class FileOperationsTest {

    @Test
    void getInputFileTest() {
        File dir = new File("/Users/victorlinard/IdeaProjects/java_cpam_project/src/test/resources/in/");

        assertTrue(FileOperations.getInputFile(dir) instanceof ArrayList);
    }

    @Test
    void readCsvTest() {
        File dir = new File("/Users/victorlinard/IdeaProjects/java_cpam_project/src/test/resources/in/");

        assertTrue(FileOperations.readCsv(FileOperations.getInputFile(dir)) instanceof ArrayList);
    }

    @Test
    void prepareForInsertTest() {
        String[] tmpUser = {"Field1", "Field2", "Field3", "Field4", "Field5", "Field6", "Field7", "Field8", "Field9"};
        String[] preparedUser = FileOperations.prepareForInsert(tmpUser, "timestamp");
        assertEquals(preparedUser.length, 10);
    }
}
