package Panel;

import Conn.ConnexionMySQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HistoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtDescription, txtDate;
    private JComboBox<String> eventTypeCombo;
    private JComboBox<Integer> hardwareCombo;

    public HistoryPanel() {
        setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        inputPanel.add(new JLabel("Hardware:"));
        hardwareCombo = new JComboBox<>();
        loadHardware();
        inputPanel.add(hardwareCombo);

        inputPanel.add(new JLabel("Event Type:"));
        eventTypeCombo = new JComboBox<>(new String[]{"Maintenance", "Repair", "Update", "Other"});
        inputPanel.add(eventTypeCombo);

        inputPanel.add(new JLabel("Description:"));
        txtDescription = new JTextField();
        inputPanel.add(txtDescription);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField();
        inputPanel.add(txtDate);

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
        String[] columns = {"ID", "Hardware ID", "Event Type", "Description", "Date"};
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
        addButton.addActionListener(e -> addHistory());
        updateButton.addActionListener(e -> updateHistory());
        deleteButton.addActionListener(e -> deleteHistory());
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM history")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("history_id"),
                        rs.getInt("hardware_id"),
                        rs.getString("event_type"),
                        rs.getString("event_description"),
                        rs.getDate("event_date")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addHistory() {
        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO history (hardware_id, event_type, event_description, event_date) VALUES (?, ?, ?, ?)")) {

            pstmt.setInt(1, (Integer)hardwareCombo.getSelectedItem());
            pstmt.setString(2, eventTypeCombo.getSelectedItem().toString());
            pstmt.setString(3, txtDescription.getText());
            pstmt.setDate(4, Date.valueOf(txtDate.getText()));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "History entry added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding history entry: " + e.getMessage());
        }
    }

    private void updateHistory() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update!");
            return;
        }

        try (Connection conn = ConnexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE history SET hardware_id=?, event_type=?, event_description=?, event_date=? WHERE history_id=?")) {

            pstmt.setInt(1, (Integer)hardwareCombo.getSelectedItem());
            pstmt.setString(2, eventTypeCombo.getSelectedItem().toString());
            pstmt.setString(3, txtDescription.getText());
            pstmt.setDate(4, Date.valueOf(txtDate.getText()));
            pstmt.setInt(5, (Integer)model.getValueAt(row, 0));

            pstmt.executeUpdate();
            loadData();
            clearFields();
            JOptionPane.showMessageDialog(this, "History entry updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating history entry: " + e.getMessage());
        }
    }

    private void deleteHistory() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this history entry?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = ConnexionMySQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM history WHERE history_id=?")) {

                pstmt.setInt(1, (Integer)model.getValueAt(row, 0));
                pstmt.executeUpdate();
                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "History entry deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting history entry: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        hardwareCombo.setSelectedIndex(0);
        eventTypeCombo.setSelectedIndex(0);
        txtDescription.setText("");
        txtDate.setText("");
        table.clearSelection();
    }

    private void selectRow(int row) {
        hardwareCombo.setSelectedItem(model.getValueAt(row, 1));
        eventTypeCombo.setSelectedItem(model.getValueAt(row, 2));
        txtDescription.setText(model.getValueAt(row, 3).toString());
        txtDate.setText(model.getValueAt(row, 4).toString());
    }
}