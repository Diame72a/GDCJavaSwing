package Panel;

// SoftwarePanel.java
import Conn.ConnexionMySQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SoftwarePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtVersion, txtLicenseType, txtLicenseExpiry;
    private JComboBox<Integer> hardwareCombo;

    public SoftwarePanel() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("Software Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Version:"));
        txtVersion = new JTextField();
        inputPanel.add(txtVersion);

        inputPanel.add(new JLabel("License Type:"));
        txtLicenseType = new JTextField();
        inputPanel.add(txtLicenseType);

        inputPanel.add(new JLabel("License Expiry (YYYY-MM-DD):"));
        txtLicenseExpiry = new JTextField();
        inputPanel.add(txtLicenseExpiry);

        inputPanel.add(new JLabel("Hardware ID:"));
        hardwareCombo = new JComboBox<>();
        loadHardware();
        inputPanel.add(hardwareCombo);

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
        String[] columns = {"ID", "Name", "Version", "License Type", "License Expiry", "Hardware ID"};
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
        addButton.addActionListener(e -> addSoftware());
        updateButton.addActionListener(e -> updateSoftware());
        deleteButton.addActionListener(e -> deleteSoftware());
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

    private void loadHardware() {
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT hardware_id FROM hardware")) {

            hardwareCombo.removeAllItems();
            while (rs.next()) {
                hardwareCombo.addItem(rs.getInt("hardware_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading hardware: " + e.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM software")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("software_id"),
                        rs.getString("software_name"),
                        rs.getString("version"),
                        rs.getString("license_type"),
                        rs.getDate("license_expiry"),
                        rs.getInt("hardware_id")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addSoftware() {
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO software (software_name, version, license_type, license_expiry, hardware_id) " +
                             "VALUES (?, ?, ?, ?, ?)")) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtVersion.getText());
            pstmt.setString(3, txtLicenseType.getText());
            pstmt.setDate(4, Date.valueOf(txtLicenseExpiry.getText()));
            pstmt.setInt(5, (Integer)hardwareCombo.getSelectedItem());

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Software added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding software: " + e.getMessage());
        }
    }

    private void updateSoftware() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE software SET software_name=?, version=?, license_type=?, license_expiry=?, " +
                             "hardware_id=? WHERE software_id=?")) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtVersion.getText());
            pstmt.setString(3, txtLicenseType.getText());
            pstmt.setDate(4, Date.valueOf(txtLicenseExpiry.getText()));
            pstmt.setInt(5, (Integer)hardwareCombo.getSelectedItem());
            pstmt.setInt(6, (Integer)model.getValueAt(row, 0));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Software updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating software: " + e.getMessage());
        }
    }

    private void deleteSoftware() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this software?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnexionMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM software WHERE software_id=?")) {

                pstmt.setInt(1, (Integer)model.getValueAt(row, 0));
                pstmt.executeUpdate();
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Software deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting software: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtVersion.setText("");
        txtLicenseType.setText("");
        txtLicenseExpiry.setText("");
        hardwareCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void selectRow(int row) {
        txtName.setText(model.getValueAt(row, 1).toString());
        txtVersion.setText(model.getValueAt(row, 2).toString());
        txtLicenseType.setText(model.getValueAt(row, 3).toString());
        txtLicenseExpiry.setText(model.getValueAt(row, 4).toString());
        hardwareCombo.setSelectedItem(model.getValueAt(row, 5));
    }
}

