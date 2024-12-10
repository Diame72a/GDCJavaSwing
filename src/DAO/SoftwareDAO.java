package DAO;

import Conn.ConnexionMySQL;

import java.sql.*;
import java.util.ArrayList;

public class SoftwareDAO {

    public ArrayList<String[]> readSoftware() {
        ArrayList<String[]> softwareList = new ArrayList<>();
        String query = "SELECT * FROM software";
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                softwareList.add(new String[]{
                        String.valueOf(rs.getInt("software_id")),
                        rs.getString("software_name"),
                        rs.getString("version"),
                        rs.getString("license_key")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return softwareList;
    }

    public void createSoftware(String name, String version, String licenseKey) {
        String query = "INSERT INTO software (software_name, version, license_key) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, version);
            stmt.setString(3, licenseKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSoftware(int id) {
        String query = "DELETE FROM software WHERE software_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSoftware(int id, String name, String version, String licenseKey) {
        String query = "UPDATE software SET software_name = ?, version = ?, license_key = ? WHERE software_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, version);
            stmt.setString(3, licenseKey);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
