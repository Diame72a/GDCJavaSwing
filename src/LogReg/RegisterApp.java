package LogReg;

import DAO.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterApp {
    public static void main(String[] args) {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setUndecorated(true);

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
        JLabel titleLabel = new JLabel("S'inscrire");
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
        gbc.gridy = 3;
        contentPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);

        // Champ Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        confirmPasswordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        contentPanel.add(confirmPasswordField, gbc);

        // Bouton Password
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(0, 123, 255));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.setOpaque(true);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action Bouton Password
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(registerFrame, "Passwords do not match!");
                    return;
                }

                UserDAO userDAO = new UserDAO();
                if (userDAO.register(username, password)) {
                    JOptionPane.showMessageDialog(registerFrame, "Registration Successful");
                    registerFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(registerFrame, "Registration Failed");
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(registerButton, gbc);

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
        registerFrame.add(mainPanel);
        registerFrame.setSize(500, 400);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setVisible(true);
    }
}
