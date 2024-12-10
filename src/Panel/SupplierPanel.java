package Panel;

import Conn.ConnexionMySQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SupplierPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtContactDetails;

    public SupplierPanel() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        inputPanel.add(new JLabel("Supplier Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Contact Details:"));
        txtContactDetails = new JTextField();
        inputPanel.add(txtContactDetails);

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
        String[] columns = {"ID", "Name", "Contact Details"};
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
        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM supplier")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_details")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addSupplier() {
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO supplier (supplier_name, contact_details) VALUES (?, ?)")) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtContactDetails.getText());

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Supplier added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding supplier: " + e.getMessage());
        }
    }

    private void updateSupplier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE supplier SET supplier_name=?, contact_details=? WHERE supplier_id=?")) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtContactDetails.getText());
            pstmt.setInt(3, (Integer)model.getValueAt(row, 0));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating supplier: " + e.getMessage());
        }
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnexionMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM supplier WHERE supplier_id=?")) {

                pstmt.setInt(1, (Integer)model.getValueAt(row, 0));
                pstmt.executeUpdate();
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting supplier: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtContactDetails.setText("");
        table.clearSelection();
    }

    private void selectRow(int row) {
        txtName.setText(model.getValueAt(row, 1).toString());
        txtContactDetails.setText(model.getValueAt(row, 2).toString());
    }
}