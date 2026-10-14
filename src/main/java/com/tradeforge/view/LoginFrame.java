package com.tradeforge.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * VIEW: Login Screen
 * Simple, student-friendly Swing GUI for user authentication with DB settings dialog.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton dbSettingsButton;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("TradeForge — Design Pattern Lab Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        rootPanel.setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("TRADEFORGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 58, 138));

        JLabel subtitleLabel = new JLabel("Stock Trading Simulation — Design Pattern Lab", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = new JTextField("anshul", 15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField("1234", 15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(passwordField, gbc);

        // Error message
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        errorLabel.setForeground(new Color(220, 38, 38));
        formPanel.add(errorLabel, gbc);

        rootPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("Login to TradeForge");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(37, 99, 235));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(100, 38));

        dbSettingsButton = new JButton("⚙ Configure MySQL Password / Settings");
        dbSettingsButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dbSettingsButton.setBackground(new Color(241, 245, 249));
        dbSettingsButton.setForeground(new Color(51, 65, 85));
        dbSettingsButton.setFocusPainted(false);
        dbSettingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dbSettingsButton.addActionListener(e -> {
            DatabaseConfigDialog dialog = new DatabaseConfigDialog(this);
            dialog.setVisible(true);
            setErrorMessage(""); // Clear previous error
        });

        JLabel hintLabel = new JLabel("Demo Credentials: anshul / 1234", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(new Color(107, 114, 128));

        buttonPanel.add(loginButton);
        buttonPanel.add(dbSettingsButton);
        buttonPanel.add(hintLabel);

        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setErrorMessage(String message) {
        if (message != null && message.contains("Access denied")) {
            errorLabel.setText("<html><center>Database access denied for MySQL user.<br>Click <b>'⚙ Configure MySQL Password'</b> below.</center></html>");
        } else {
            errorLabel.setText(message);
        }
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
        passwordField.addActionListener(listener);
    }
}
