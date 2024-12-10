package Panel;

import Conn.ConnexionMySQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ContractPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtType, txtStartDate, txtEndDate;
    private JComboBox<Integer> supplierCombo;

    public ContractPanel() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        inputPanel.add(new JLabel("Contract Type:"));
        txtType = new JTextField();
        inputPanel.add(txtType);

        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        txtStartDate = new JTextField();
        inputPanel.add(txtStartDate);

        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        txtEndDate = new JTextField();
        inputPanel.add(txtEndDate);

        inputPanel.add(new JLabel("Supplier:"));
        supplierCombo = new JComboBox<>();
        loadSuppliers();
        inputPanel.add(supplierCombo);

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
        String[] columns = {"ID", "Type", "Start Date", "End Date", "Supplier ID"};
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
        addButton.addActionListener(e -> addContract());
        updateButton.addActionListener(e -> updateContract());
        deleteButton.addActionListener(e -> deleteContract());
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

    private void loadSuppliers() {
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT supplier_id FROM supplier")) {

            supplierCombo.removeAllItems();
            while (rs.next()) {
                supplierCombo.addItem(rs.getInt("supplier_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = ConnexionMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM contract")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("contract_id"),
                        rs.getString("contract_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getInt("supplier_id")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addContract() {
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO contract (contract_type, start_date, end_date, supplier_id) VALUES (?, ?, ?, ?)")) {

            pstmt.setString(1, txtType.getText());
            pstmt.setDate(2, Date.valueOf(txtStartDate.getText()));
            pstmt.setDate(3, Date.valueOf(txtEndDate.getText()));
            pstmt.setInt(4, (Integer)supplierCombo.getSelectedItem());

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Contract added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding contract: " + e.getMessage());
        }
    }

    private void updateContract() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE contract SET contract_type=?, start_date=?, end_date=?, supplier_id=? WHERE contract_id=?")) {

            pstmt.setString(1, txtType.getText());
            pstmt.setDate(2, Date.valueOf(txtStartDate.getText()));
            pstmt.setDate(3, Date.valueOf(txtEndDate.getText()));
            pstmt.setInt(4, (Integer)supplierCombo.getSelectedItem());
            pstmt.setInt(5, (Integer)model.getValueAt(row, 0));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Contract updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating contract: " + e.getMessage());
        }
    }

    private void deleteContract() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this contract?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnexionMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM contract WHERE contract_id=?")) {

                pstmt.setInt(1, (Integer)model.getValueAt(row, 0));
                pstmt.executeUpdate();
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Contract deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting contract: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtType.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        supplierCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void selectRow(int row) {
        txtType.setText(model.getValueAt(row, 1).toString());
        txtStartDate.setText(model.getValueAt(row, 2).toString());
        txtEndDate.setText(model.getValueAt(row, 3).toString());
        supplierCombo.setSelectedItem(model.getValueAt(row, 4));
    }
}