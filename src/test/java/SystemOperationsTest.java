import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SystemOperationsTest {

    @Test
    void getPreviousDirTest() {
        assertEquals("/Users/victorlinard/IdeaProjects/java_cpam_project/src/test/resources/", SystemOperations.getPreviousDir("/Users/victorlinard/IdeaProjects/java_cpam_project/src/test/resources/in/"));
    }

    @Test
    void pathVerificationTest() {
        assertNotNull(SystemOperations.pathVerification(new String[]{"/Users/victorlinard/IdeaProjects/java_cpam_project/src/test/resources/in/"}));
    }
}
