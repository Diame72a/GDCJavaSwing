package DAO;

import Conn.ConnexionMySQL;
import java.sql.*;
import java.util.ArrayList;

public class ContractDAO {

    public ArrayList<String[]> readContracts() {
        ArrayList<String[]> contracts = new ArrayList<>();
        String query = "SELECT * FROM contract";
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                contracts.add(new String[]{
                        String.valueOf(rs.getInt("contract_id")),
                        rs.getString("contract_type"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        String.valueOf(rs.getInt("supplier_id"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public void createContract(String type, String start, String end, int supplierId) {
        String query = "INSERT INTO contract (contract_type, start_date, end_date, supplier_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            stmt.setInt(4, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateContract(int id, String type, String start, String end, int supplierId) {
        String query = "UPDATE contract SET contract_type = ?, start_date = ?, end_date = ?, supplier_id = ? WHERE contract_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            stmt.setInt(4, supplierId);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteContract(int id) {
        String query = "DELETE FROM contract WHERE contract_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
