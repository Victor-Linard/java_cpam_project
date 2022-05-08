import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // Instanciation d'une variable de type DatabaseOperations pour accéder à la base de données
        DatabaseOperations dbOp = new DatabaseOperations("localhost", "5432", "victorlinard", "victorlinard", "JAs++1qsn");

        dbOp.openConnection();
        if (dbOp.getConnection() != null)
            dbOp.createStatement();

        // Si on a bien accès à la base de données on continue avec les fichiers
        if (dbOp.getStatement() != null) {
            // On vérifie si le chemin données est bien un répertoire
            File dir = SystemOperations.pathVerification(args);
            // Récupération des fichiers csv
            ArrayList<File> usersCsv = FileOperations.getInputFile(dir);
            // Lecture et traitement des fichiers csv
            ArrayList<String[]> usersCsvDataValid = FileOperations.readCsv(usersCsv);

            // Envoie dans la base de données
            dbOp.arrayListToDb(usersCsvDataValid);
            dbOp.closeConnection();
        }
    }
}
