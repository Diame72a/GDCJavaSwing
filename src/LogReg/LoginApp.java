package LogReg;

import DAO.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginApp {
    public static void main(String[] args) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(85, 170, 255), 0, getHeight(), new Color(0, 85, 170)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Ajout de marges

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Fond transparent pour le contenu
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre
        JLabel titleLabel = new JLabel("Se connecter");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(titleLabel, gbc);

        // Champ Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        contentPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        contentPanel.add(usernameField, gbc);

        // Champ Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);

        // Bouton Login
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setOpaque(true);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                UserDAO userDAO = new UserDAO();
                if (userDAO.authenticate(username, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Login Successful");
                    loginFrame.dispose();

                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password");
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(loginButton, gbc);

        // Panneau supérieur pour le bouton de fermeture
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // Fond transparent

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(255, 77, 77));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setOpaque(true);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));

        // Aligner la croix à droite
        topPanel.add(closeButton, BorderLayout.EAST);

        // Ajouter les panneaux au panneau principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Configuration de la fenêtre
        loginFrame.add(mainPanel);
        loginFrame.setSize(450, 300);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }
}
