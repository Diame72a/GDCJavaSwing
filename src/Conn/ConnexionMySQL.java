package Conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionMySQL {
    private static final String URL = "jdbc:mysql://localhost:3306/AssetManagement";
    private static final String UTILISATEUR = "root";
    private static final String MOT_DE_PASSE = "1234";

    // Méthode statique pour récupérer une connexion
    public static Connection getConnection() throws SQLException {
        try {
            // Charger le pilote JDBC (inutile dans les versions modernes de Java, mais laissé ici pour compatibilité)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé.", e);
        }
        // Retourner une connexion
        return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
    }
}
