import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileOperations {
    /**
     * Create ArrayList if Fiels of the valids files into the directory.
     * @param dir the given directory
     * @return ArrayList of File
     */
    public static ArrayList<File> getInputFile(File dir) {
        File[] liste = dir.listFiles();
        ArrayList<File> usersCsv = new ArrayList<>();
        if (liste != null)
            for (File item : liste)
                if (item.isFile() && match("^users_\\d{14}.csv$", item.getName()))
                    usersCsv.add(item);
        return usersCsv;
    }

    /**
     * Shortcut function to use a regex.
     * @param pattern the regex
     * @param matcher the string you want to check
     * @return boolean
     */
    public static boolean match(String pattern, String matcher) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(matcher);
        return m.matches();
    }

    /**
     * Read csv files one by one and lines one by one.
     * Sort lines into valid and invalid format.
     * Write bad lines into the error directory.
     * @param usersCsv ArrayList of File to read
     * @return ArrayList of String[] with the valid entries
     */
    public static ArrayList<String[]> readCsv(ArrayList<File> usersCsv) {
        ArrayList<String[]> usersCsvDataValid = new ArrayList<>();
        String path = SystemOperations.getPreviousDir(usersCsv.get(0).getPath().split("users_")[0]);
        SystemOperations.createErrorOutDir(path);

        for (File csv : usersCsv) {
            ArrayList<String[]> usersCsvDataInvalid = new ArrayList<>();
            try (CSVReader reader = new CSVReader(new FileReader(csv))) {
                String[] tmpUser;
                while ((tmpUser = reader.readNext()) != null) {
                    if (inputFormatCorrect(tmpUser)) {
                        if (!tmpUser[8].equals("Montant_Remboursement")) {
                            String[] user = prepareForInsert(tmpUser, csv.getName().split("_")[1].split("\\.")[0]);
                            usersCsvDataValid.add(user);
                        }
                    } else {
                        // Si on a autre chose que "Numero_Securite_Sociale" dans tmpUSer[0] alors il y a une erreur
                        if (tmpUser[0].length() != 24)
                            usersCsvDataInvalid.add(tmpUser);
                    }
                }
                if (!usersCsvDataInvalid.isEmpty())
                    writeCsv(usersCsvDataInvalid, path, csv);
            } catch (IOException | CsvValidationException e) {
                System.err.println("Erreur de lecture ou d'écriture du fichier csv.");
            }

            try {
                Files.move(Paths.get(csv.getPath()), Paths.get(path+"out/"+csv.getName()));
            } catch (IOException e) { System.err.println("Erreur de déplacement des fichiers csv."); }
        }

        return usersCsvDataValid;
    }

    /**
     * Check for data format with regex
     * @param user String[] that correspond to a line of a csv
     * @return boolean
     */
    public static boolean inputFormatCorrect(String[] user) {
        boolean formatCorrect = true;
        for (int i=0; i<user.length; i++) {
            switch (i) {
                case 0: formatCorrect = match("^[1|2]\\d{14}$", user[i]); break;
                case 1:
                case 2: formatCorrect = match("^\\D*$", user[i]); break;
                case 3: formatCorrect = match("^\\d{4}-[0-1][0-9]-[0-3][0-9]$", user[i]); break;
                case 4: formatCorrect = match("^0[1-9]\\d{8}$", user[i]); break;
                case 5: formatCorrect = match("^\\S+@.+.\\D+$", user[i]); break;
            }
        }
        return formatCorrect;
    }

    /**
     * Add the timestamp at the end of the user info
     * @param tmpUser the user to modify
     * @param timestamp the timestamp to add
     * @return String[] with the user info ready to insert
     */
    public static String[] prepareForInsert(String[] tmpUser, String timestamp) {
        String[] user = new String[10];
        for (int i = 0; i < tmpUser.length; i++)
            user[i] = tmpUser[i];
        user[tmpUser.length] = timestamp;
        return user;
    }

    /**
     * Write a csv file in the error folder with all the line with incorrect formats
     * @param usersCsvDataInvalid all the incorrect line
     * @param path to the ressource folder
     * @param csv the csv file where the error was
     */
    public static void writeCsv(ArrayList<String[]> usersCsvDataInvalid, String path, File csv) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path+"error/"+csv.getName()))) {
            writer.writeAll(usersCsvDataInvalid);
        } catch (IOException e) {
            System.err.println("Erreur d'écriture du fichier csv d'erreur.");
        }
    }
}
