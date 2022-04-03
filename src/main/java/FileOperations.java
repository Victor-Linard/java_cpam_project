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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileOperations {
    public static ArrayList<File> getInputFile(File dir) {
        File[] liste = dir.listFiles();
        ArrayList<File> usersCsv = new ArrayList<>();
        if (liste != null)
            for(File item :  liste)
                if(item.isFile() && match("^users_\\d{14}.csv$", item.getName()))
                    usersCsv.add(item);
        return usersCsv;
    }

    public static boolean match(String pattern, String matcher) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(matcher);
        return m.matches();
    }

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
                        String[] user = prepareForInsert(tmpUser, csv.getName().split("_")[1].split("\\.")[0]);
                        usersCsvDataValid.add(user);
                    } else {
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

    public static boolean inputFormatCorrect(String[] user) {
        boolean formatCorrect = true;
        for (int i=0; i<user.length; i++) {
            switch (i) {
                case 0: formatCorrect = !match("^[1|2]\\d{14}$", user[i]); break;
                case 1:
                case 2: formatCorrect = !match("^\\D*$", user[i]); break;
                case 3: formatCorrect = !match("^\\d{4}-[0-1][0-9]-[0-3][0-9]$", user[i]); break;
                case 4: formatCorrect = !match("^0[1-9]\\d{8}$", user[i]); break;
                case 5: formatCorrect = !match("^\\w+@\\w+.\\D+$", user[i]); break;
            }
        }
        return formatCorrect;
    }

    public static String[] prepareForInsert(String[] tmpUser, String timestamp) {
        String[] user = new String[10];
        for (int i = 0; i < tmpUser.length; i++)
            user[i] = tmpUser[i];
        user[tmpUser.length] = timestamp;
        return user;
    }

    public static void writeCsv(ArrayList<String[]> usersCsvDataInvalid, String path, File csv) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path+"error/"+csv.getName()))) {
            writer.writeAll(usersCsvDataInvalid);
        } catch (IOException e) {
            System.err.println("Erreur d'écriture du fichier csv d'erreur.");
        }
    }
}
