package com.tradeforge.view;

import com.tradeforge.singleton.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Dialog enabling students to configure MySQL credentials directly from the GUI
 * without needing command-line parameters or environment variables.
 */
public class DatabaseConfigDialog extends JDialog {

    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public DatabaseConfigDialog(Frame owner) {
        super(owner, "MySQL Database Settings", true);
        setSize(520, 320);
        setLocationRelativeTo(owner);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 20, 18, 20));
        root.setBackground(new Color(248, 250, 252));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 2, 2));
        header.setOpaque(false);
        JLabel title = new JLabel("Configure MySQL Connection", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));

        JLabel subtitle = new JLabel("Enter your local MySQL credentials (these will be saved in tradeforge.properties)");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(100, 116, 139));

        header.add(title);
        header.add(subtitle);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DatabaseConnection db = DatabaseConnection.getInstance();

        // JDBC URL
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblUrl = new JLabel("JDBC URL:");
        lblUrl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblUrl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        urlField = new JTextField(db.getUrl(), 20);
        urlField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(urlField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        userField = new JTextField(db.getUsername(), 20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(userField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblPass, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        passField = new JPasswordField(db.getPassword(), 20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(passField, gbc);

        // Status Label
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        statusLabel = new JLabel("Click 'Test Connection' to verify credentials.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(71, 85, 105));
        form.add(statusLabel, gbc);

        root.add(form, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton testBtn = new JButton("Test Connection");
        testBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        testBtn.addActionListener(e -> testConnection());

        JButton saveBtn = new JButton("Save & Apply");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(new Color(37, 99, 235));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveCredentials());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.addActionListener(e -> dispose());

        actions.add(testBtn);
        actions.add(saveBtn);
        actions.add(cancelBtn);

        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void testConnection() {
        String testUrl = urlField.getText().trim();
        String testUser = userField.getText().trim();
        String testPass = new String(passField.getPassword());

        try {
            DatabaseConnection.testCredentials(testUrl, testUser, testPass);
            statusLabel.setText("✔ Connection Successful! Click 'Save & Apply'.");
            statusLabel.setForeground(new Color(16, 185, 129));
        } catch (SQLException ex) {
            statusLabel.setText("✖ Failed: " + ex.getMessage());
            statusLabel.setForeground(new Color(220, 38, 38));
        }
    }

    private void saveCredentials() {
        String newUrl = urlField.getText().trim();
        String newUser = userField.getText().trim();
        String newPass = new String(passField.getPassword());

        DatabaseConnection.getInstance().saveConfiguration(newUrl, newUser, newPass);
        JOptionPane.showMessageDialog(this, "Credentials saved successfully to tradeforge.properties!",
                "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
