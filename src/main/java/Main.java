import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        File dir = SystemOperations.pathVerification(args);
        ArrayList<File> usersCsv = FileOperations.getInputFile(dir);

        ArrayList<String[]> usersCsvDataInvalid = FileOperations.readCsv(usersCsv);
        usersCsvDataInvalid.removeIf(n -> (n[0].charAt(0) == 'N'));

        DatabaseOperations dbOp = new DatabaseOperations("localhost", "5432", "victorlinard", "victorlinard", "JAs++1qsn");

        if (dbOp.getConnection() == null || dbOp.getStatement() == null) {
            dbOp.openConnection();
            dbOp.createStatement();
        }
        dbOp.arrayListToDb(usersCsvDataInvalid);
        dbOp.closeConnection();
    }
}
