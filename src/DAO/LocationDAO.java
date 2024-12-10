package DAO;

import Conn.ConnexionMySQL;

import java.sql.*;
import java.util.ArrayList;

public class LocationDAO {

    public ArrayList<String[]> readLocations() {
        ArrayList<String[]> locationList = new ArrayList<>();
        String query = "SELECT * FROM location";
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                locationList.add(new String[]{
                        String.valueOf(rs.getInt("location_id")),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("country")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locationList;
    }

    public void createLocation(String address, String city, String country) {
        String query = "INSERT INTO location (address, city, country) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, address);
            stmt.setString(2, city);
            stmt.setString(3, country);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLocation(int id) {
        String query = "DELETE FROM location WHERE location_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLocation(int id, String address, String city, String country) {
        String query = "UPDATE location SET address = ?, city = ?, country = ? WHERE location_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, address);
            stmt.setString(2, city);
            stmt.setString(3, country);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
