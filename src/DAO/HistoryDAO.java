package DAO;

import Conn.ConnexionMySQL;

import java.sql.*;
import java.util.ArrayList;

public class HistoryDAO {

    public ArrayList<String[]> readHistories() {
        ArrayList<String[]> historyList = new ArrayList<>();
        String query = "SELECT * FROM history";
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                historyList.add(new String[]{
                        String.valueOf(rs.getInt("history_id")),
                        rs.getString("event_date"),
                        rs.getString("description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    public void createHistory(String eventDate, String description) {
        String query = "INSERT INTO history (event_date, description) VALUES (?, ?)";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eventDate);
            stmt.setString(2, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHistory(int id) {
        String query = "DELETE FROM history WHERE history_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateHistory(int id, String eventDate, String description) {
        String query = "UPDATE history SET event_date = ?, description = ? WHERE history_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eventDate);
            stmt.setString(2, description);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
