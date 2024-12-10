package DAO;

import Conn.ConnexionMySQL;
import java.sql.*;
import java.util.ArrayList;

public class HardwareDAO {

    public ArrayList<String[]> readHardware() {
        ArrayList<String[]> hardwareList = new ArrayList<>();
        String query = "SELECT * FROM hardware";
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                hardwareList.add(new String[]{
                        String.valueOf(rs.getInt("hardware_id")),
                        rs.getString("hardware_name"),
                        rs.getString("model"),
                        rs.getString("serial_number")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hardwareList;
    }

    public void createHardware(String name, String model, String serialNumber) {
        String query = "INSERT INTO hardware (hardware_name, model, serial_number) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, model);
            stmt.setString(3, serialNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateHardware(int id, String name, String model, String serialNumber) {
        String query = "UPDATE hardware SET hardware_name = ?, model = ?, serial_number = ? WHERE hardware_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, model);
            stmt.setString(3, serialNumber);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHardware(int id) {
        String query = "DELETE FROM hardware WHERE hardware_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
