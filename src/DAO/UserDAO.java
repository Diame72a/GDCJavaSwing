package DAO;

import Conn.ConnexionMySQL;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Session.UserSession;

public class UserDAO {

    public boolean authenticate(String username, String password) {
        String query = "SELECT id FROM user WHERE user_name = ? AND password = ?";
        try (Connection connexion = ConnexionMySQL.getConnection();
             PreparedStatement stmt = connexion.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    UserSession.getInstance().setUserId(userId);
                    return true;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password) {
        String regQuery = "INSERT INTO user (user_name, password) VALUES (?, ?)";
        try(Connection connexion = ConnexionMySQL.getConnection();
        PreparedStatement regStmt = connexion.prepareStatement(regQuery)) {
            regStmt.setString(1, username);
            regStmt.setString(2, password);

            int rowsAffected = regStmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
