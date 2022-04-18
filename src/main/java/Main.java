import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        DatabaseOperations dbOp = new DatabaseOperations("localhost", "5432", "victorlinard", "victorlinard", "JAs++1qsn");

        dbOp.openConnection();
        if (dbOp.getConnection() != null)
            dbOp.createStatement();


        if (dbOp.getStatement() != null) {
            File dir = SystemOperations.pathVerification(args);
            ArrayList<File> usersCsv = FileOperations.getInputFile(dir);

            ArrayList<String[]> usersCsvDataInvalid = FileOperations.readCsv(usersCsv);
            usersCsvDataInvalid.removeIf(n -> (n[0].charAt(0) == 'N'));

            dbOp.arrayListToDb(usersCsvDataInvalid);
            dbOp.closeConnection();
        }
    }
}
