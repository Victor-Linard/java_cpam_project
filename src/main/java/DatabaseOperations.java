import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseOperations {
    private final String hostname;
    private final String port;
    private final String database;
    private final String user;
    private final String password;
    private Connection connection;
    private Statement statement;
    private static final String baseUrl = "jdbc:postgresql://";

    /**
     * Constructor of the DatabaseOperations object to access the database
     * @param hostname ip address or domain name
     * @param port port of the db
     * @param database name of the db
     * @param user username of the db
     * @param password password of the db
     */
    public DatabaseOperations(String hostname, String port, String database, String user, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    /**
     * Open the connection to the database
     */
    public void openConnection() {
        try{
            Class.forName ("org.postgresql.Driver");
            System.out.println("Chargement du driver");
        }
        catch (ClassNotFoundException e) {System.err.println("Erreur de chargement du driver.");}

        try {
            this.connection = DriverManager.getConnection(baseUrl+this.hostname+":"+this.port+"/"+this.database, this.user, this.password);
            System.out.println("Connexion à la base de données réussie : " + this.connection);
        } catch(SQLException e) {System.err.println("Erreur de conenxion à la base de données.");}
    }

    /**
     * Create the statement
     */
    public void createStatement() {
        try {
            this.statement = this.connection.createStatement();
            System.out.println("Création du statement réussie : " + this.statement);
        } catch(SQLException e) {System.err.println("Erreur de création du statement.");}
    }

    /**
     * Close the connection to the db
     */
    public void closeConnection() {
        try {
            this.connection.close();
            this.connection = null;
            this.statement = null;
            System.out.println("Fermeture de la connexion.");
        } catch(SQLException e) {System.err.println("Erreur à la fermeture de la connexion.");}
    }

    /**
     * Check if the remboursement id exist in the db
     * @param idR id remboursement
     * @return boolean
     */
    public boolean idRemboursementExist(String idR) {
        boolean exist = false;
        try {
            PreparedStatement pstmt = this.connection.prepareStatement("SELECT COUNT(id_remboursement) AS countIdR FROM cpam WHERE id_remboursement=?;");
            pstmt.setString(1, idR);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                exist = rs.getInt("countIdR") == 1;
            }
        } catch(SQLException e) {System.err.println("Erreur de récupération d'un id_remboursement existant.");}
        return exist;
    }

    /**
     * For each user, decide if there is an insert or an update
     * @param usersCsvDataValid ArrayList of String[] of the valid users
     */
    public void arrayListToDb(ArrayList<String[]> usersCsvDataValid) {
        //cleanDb();
        for (String[] user : usersCsvDataValid)
            if (idRemboursementExist(user[6]))
                insertUser(user, "update");
            else
                insertUser(user, "insert");
    }

    /**
     * Delete the entire db
     */
    public void cleanDb() {
        try {
            this.statement.executeUpdate("DELETE FROM cpam;");
        } catch(SQLException e) {System.err.println("Erreur de suppression des données.");}
    }

    /**
     * Insert or update users
     * @param user the user to add or modify
     * @param action insert or update
     */
    public void insertUser(String[] user, String action) {
        try {
            String sql = "";
            if (action.equals("insert"))
                sql = "INSERT INTO cpam (numero_securite_social, prenom, nom, date_naissance, numero_telephone, e_mail, id_remboursement, code_soin, montant_remboursement, file_date) VALUES (?,?,?,?,?,?,?,?,?,?);";
            if (action.equals("update"))
                sql = "UPDATE cpam SET numero_securite_social=?, prenom=?, nom=?, date_naissance=?, numero_telephone=?, e_mail=?, id_remboursement=?, code_soin=?, montant_remboursement=?, file_date=? WHERE id_remboursement=?;";

            PreparedStatement pstmt = this.connection.prepareStatement(sql);
            pstmt.setString(1, user[0]);
            pstmt.setString(2, user[1]);
            pstmt.setString(3, user[2]);
            pstmt.setString(4, user[3]);
            pstmt.setString(5, user[4]);
            pstmt.setString(6, user[5]);
            pstmt.setString(7, user[6]);
            pstmt.setString(8, user[7]);
            pstmt.setDouble(9, Double.parseDouble(user[8]));
            pstmt.setString(10, user[9]);
            if (action.equals("update"))
                pstmt.setString(11, user[6]);
            pstmt.executeUpdate();
        } catch(SQLException e) {System.err.println("Erreur à l'insertion ou mise à jour des enregistrements.");}
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
