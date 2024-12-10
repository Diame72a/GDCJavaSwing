package Panel;

// LocationPanel.java
import Conn.ConnexionMySQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LocationPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtFloorName, txtBuilding, txtAddress;

    public LocationPanel() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        inputPanel.add(new JLabel("Floor Name:"));
        txtFloorName = new JTextField();
        inputPanel.add(txtFloorName);

        inputPanel.add(new JLabel("Building:"));
        txtBuilding = new JTextField();
        inputPanel.add(txtBuilding);

        inputPanel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        inputPanel.add(txtAddress);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Create table
        String[] columns = {"ID", "Floor Name", "Building", "Address"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add components to panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadData();

        // Add button listeners
        addButton.addActionListener(e -> addLocation());
        updateButton.addActionListener(e -> updateLocation());
        deleteButton.addActionListener(e -> deleteLocation());
        clearButton.addActionListener(e -> clearFields());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectRow(row);
                }
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM location")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("location_id"),
                        rs.getString("floor_name"),
                        rs.getString("building"),
                        rs.getString("address")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addLocation() {
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO location (floor_name, building, address) VALUES (?, ?, ?)")) {

            pstmt.setString(1, txtFloorName.getText());
            pstmt.setString(2, txtBuilding.getText());
            pstmt.setString(3, txtAddress.getText());

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Location added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding location: " + e.getMessage());
        }
    }

    private void updateLocation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE location SET floor_name=?, building=?, address=? WHERE location_id=?")) {

            pstmt.setString(1, txtFloorName.getText());
            pstmt.setString(2, txtBuilding.getText());
            pstmt.setString(3, txtAddress.getText());
            pstmt.setInt(4, (Integer)model.getValueAt(row, 0));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Location updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating location: " + e.getMessage());
        }
    }

    private void deleteLocation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this location?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnexionMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM location WHERE location_id=?")) {

                pstmt.setInt(1, (Integer)model.getValueAt(row, 0));
                pstmt.executeUpdate();
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Location deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting location: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtFloorName.setText("");
        txtBuilding.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }

    private void selectRow(int row) {
        txtFloorName.setText(model.getValueAt(row, 1).toString());
        txtBuilding.setText(model.getValueAt(row, 2).toString());
        txtAddress.setText(model.getValueAt(row, 3).toString());
    }
}
