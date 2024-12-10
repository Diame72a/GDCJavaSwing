package DAO;

import Conn.ConnexionMySQL;
import java.sql.*;
import java.util.ArrayList;

public class SupplierDAO {

    // Lecture des fournisseurs
    public ArrayList<String[]> readSuppliers() {
        ArrayList<String[]> suppliers = new ArrayList<>();
        String query = "SELECT * FROM supplier";
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                suppliers.add(new String[]{
                        String.valueOf(rs.getInt("supplier_id")),
                        rs.getString("supplier_name"),
                        rs.getString("contact")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Création d'un nouveau fournisseur
    public void createSupplier(String name, String contact) {
        String query = "INSERT INTO supplier (supplier_name, contact) VALUES (?, ?)";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mise à jour d'un fournisseur existant
    public void updateSupplier(int id, String name, String contact) {
        String query = "UPDATE supplier SET supplier_name = ?, contact = ? WHERE supplier_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Suppression d'un fournisseur
    public void deleteSupplier(int id) {
        String query = "DELETE FROM supplier WHERE supplier_id = ?";
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
