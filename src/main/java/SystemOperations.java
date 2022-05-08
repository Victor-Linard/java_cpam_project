import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemOperations {
    /**
     * Check if the given path exist and if it is a directory
     * @param args the parameter given in the execution of the script
     * @return the directory in File type
     */
    public static File pathVerification(String[] args) {
        if (args.length == 0) {
            System.err.println("USAGE : java Main.java [PATH]");
            System.exit(1);
        }

        File dir = new File(args[0]);
        if (!dir.isDirectory()) {
            System.err.println("NOTICE : the given directory is not valid.");
            System.exit(2);
        }

        return dir;
    }

    /**
     * Create out and error directories where the input directory was given
     * @param strPath the path where we want to create the directories
     */
    public static void createErrorOutDir(String strPath) {
        File dirError = new File(strPath+"error/");
        File dirOut = new File(strPath+"out/");
        if (!dirError.isDirectory()) {
            try {
                Path path = Paths.get(strPath+"error/");
                Files.createDirectories(path);
            } catch (IOException e) { System.err.println("Erreur de création du répertoire d'erreur."); }
        }
        if (!dirOut.isDirectory()) {
            try {
                Path path = Paths.get(strPath+"out/");
                Files.createDirectories(path);
            } catch (IOException e) { System.err.println("Erreur de création du répertoire de sortie."); }
        }
    }

    /**
     * Input : /Users/victorlinard/IdeaProjects/java_cpam_project/src/main/resources/in
     * @param path the parameter given in the execution of the script
     * @return /Users/victorlinard/IdeaProjects/java_cpam_project/src/main/resources/
     */
    public static String getPreviousDir(String path) {
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < path.split("/").length-1; i++) {
            newPath.append(path.split("/")[i]);
            newPath.append("/");
        }
        return newPath.toString();
    }
}
