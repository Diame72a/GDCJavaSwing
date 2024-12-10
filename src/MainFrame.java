// MainFrame.java
import javax.swing.*;
import java.awt.*;
import Panel.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        setTitle("Asset Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Add panels for each table
        tabbedPane.addTab("Hardware", new HardwarePanel());
        tabbedPane.addTab("Software", new SoftwarePanel());
        tabbedPane.addTab("Location", new LocationPanel());
        tabbedPane.addTab("Supplier", new SupplierPanel());
        tabbedPane.addTab("Contract", new ContractPanel());
        tabbedPane.addTab("History", new HistoryPanel());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
