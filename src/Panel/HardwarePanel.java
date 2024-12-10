package Panel;

// HardwarePanel.java
import Conn.ConnexionMySQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HardwarePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtType, txtProcessor, txtRam, txtOs;
    private JComboBox<String> statusCombo;
    private JTextField txtPurchaseDate;
    private JComboBox<Integer> locationCombo, userCombo;

    public HardwarePanel() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 5, 5));

        inputPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Type:"));
        txtType = new JTextField();
        inputPanel.add(txtType);

        inputPanel.add(new JLabel("Processor:"));
        txtProcessor = new JTextField();
        inputPanel.add(txtProcessor);

        inputPanel.add(new JLabel("RAM:"));
        txtRam = new JTextField();
        inputPanel.add(txtRam);

        inputPanel.add(new JLabel("OS:"));
        txtOs = new JTextField();
        inputPanel.add(txtOs);

        inputPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"active", "inactive"});
        inputPanel.add(statusCombo);

        inputPanel.add(new JLabel("Purchase Date (YYYY-MM-DD):"));
        txtPurchaseDate = new JTextField();
        inputPanel.add(txtPurchaseDate);

        inputPanel.add(new JLabel("Location:"));
        locationCombo = new JComboBox<>();
        loadLocations();
        inputPanel.add(locationCombo);

        inputPanel.add(new JLabel("User:"));
        userCombo = new JComboBox<>();
        loadUsers();
        inputPanel.add(userCombo);

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
        String[] columns = {"ID", "Name", "Type", "Processor", "RAM", "OS", "Status", "Purchase Date", "Location", "User"};
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
        addButton.addActionListener(e -> addHardware());
        updateButton.addActionListener(e -> updateHardware());
        deleteButton.addActionListener(e -> deleteHardware());
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

    private void loadLocations() {
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT location_id FROM location")) {

            locationCombo.removeAllItems();
            while (rs.next()) {
                locationCombo.addItem(rs.getInt("location_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading locations: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM user")) {

            userCombo.removeAllItems();
            while (rs.next()) {
                userCombo.addItem(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM hardware")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("hardware_id"),
                        rs.getString("hardware_name"),
                        rs.getString("hardware_type"),
                        rs.getString("processor"),
                        rs.getInt("ram"),
                        rs.getString("os"),
                        rs.getString("status"),
                        rs.getDate("purchase_date"),
                        rs.getInt("location_id"),
                        rs.getInt("user_id")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addHardware() {
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO hardware (hardware_name, hardware_type, processor, ram, os, status, purchase_date, location_id, user_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtType.getText());
            pstmt.setString(3, txtProcessor.getText());
            pstmt.setInt(4, Integer.parseInt(txtRam.getText()));
            pstmt.setString(5, txtOs.getText());
            pstmt.setString(6, statusCombo.getSelectedItem().toString());
            pstmt.setDate(7, Date.valueOf(txtPurchaseDate.getText()));
            pstmt.setInt(8, (Integer)locationCombo.getSelectedItem());
            pstmt.setInt(9, (Integer)userCombo.getSelectedItem());

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Hardware added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding hardware: " + e.getMessage());
        }
    }

    private void updateHardware() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE hardware SET hardware_name=?, hardware_type=?, processor=?, ram=?, os=?, status=?, " +
                             "purchase_date=?, location_id=?, user_id=? WHERE hardware_id=?")) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtType.getText());
            pstmt.setString(3, txtProcessor.getText());
            pstmt.setInt(4, Integer.parseInt(txtRam.getText()));
            pstmt.setString(5, txtOs.getText());
            pstmt.setString(6, statusCombo.getSelectedItem().toString());
            pstmt.setDate(7, Date.valueOf(txtPurchaseDate.getText()));
            pstmt.setInt(8, (Integer)locationCombo.getSelectedItem());
            pstmt.setInt(9, (Integer)userCombo.getSelectedItem());
            pstmt.setInt(10, (Integer)model.getValueAt(row, 0));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Hardware updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating hardware: " + e.getMessage());
        }
    }

    private void deleteHardware() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this hardware?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnexionMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM hardware WHERE hardware_id=?")) {

                pstmt.setInt(1, (Integer)model.getValueAt(row, 0));
                pstmt.executeUpdate();
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Hardware deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting hardware: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtType.setText("");
        txtProcessor.setText("");
        txtRam.setText("");
        txtOs.setText("");
        statusCombo.setSelectedIndex(0);
        txtPurchaseDate.setText("");
        locationCombo.setSelectedIndex(0);
        userCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void selectRow(int row) {
        txtName.setText(model.getValueAt(row, 1).toString());
        txtType.setText(model.getValueAt(row, 2).toString());
        txtProcessor.setText(model.getValueAt(row, 3).toString());
        txtRam.setText(model.getValueAt(row, 4).toString());
        txtOs.setText(model.getValueAt(row, 5).toString());
        statusCombo.setSelectedItem(model.getValueAt(row, 6).toString());
        txtPurchaseDate.setText(model.getValueAt(row, 7).toString());
        locationCombo.setSelectedItem(model.getValueAt(row, 8));
        userCombo.setSelectedItem(model.getValueAt(row, 9));
    }
}
