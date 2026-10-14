package com.tradeforge.view;

import com.tradeforge.model.User;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * VIEW: Main Dashboard Window
 * Organizes the application using modular panels and tabbed navigation.
 */
public class DashboardFrame extends JFrame {

    // Child Panels
    private StocksPanel stocksPanel;
    private PortfolioPanel portfolioPanel;
    private TradePanel tradePanel;
    private TransactionHistoryPanel historyPanel;

    // Header Components
    private JLabel usernameLabel;
    private JLabel balanceLabel;
    private JLabel notificationLabel;
    private JButton refreshButton;
    private JButton logoutButton;
    private JTabbedPane tabbedPane;

    public DashboardFrame() {
        setTitle("TradeForge — Design Pattern Lab Trading Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));

        // 1. TOP HEADER BAR
        JPanel headerBar = new JPanel(new BorderLayout(15, 10));
        headerBar.setBackground(new Color(30, 41, 59));
        headerBar.setBorder(new EmptyBorder(14, 20, 14, 20));

        // App Logo & User Info
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        JLabel logo = new JLabel("TRADEFORGE");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(new Color(96, 165, 250));

        usernameLabel = new JLabel("Logged in as: User");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(226, 232, 240));

        leftHeader.add(logo);
        leftHeader.add(usernameLabel);
        headerBar.add(leftHeader, BorderLayout.WEST);

        // Balance & Actions
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        balanceLabel = new JLabel("Balance: ₹0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(52, 211, 153)); // Emerald green

        refreshButton = new JButton("↻ Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(51, 65, 85));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(239, 68, 68));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightHeader.add(balanceLabel);
        rightHeader.add(refreshButton);
        rightHeader.add(logoutButton);
        headerBar.add(rightHeader, BorderLayout.EAST);

        // 2. NOTIFICATION BANNER (OBSERVER PATTERN)
        JPanel bannerPanel = new JPanel(new BorderLayout(10, 0));
        bannerPanel.setBackground(new Color(238, 242, 255));
        bannerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(199, 210, 254), 1),
                new EmptyBorder(8, 16, 8, 16)
        ));

        JLabel observerBadge = new JLabel("[OBSERVER NOTIFICATION]");
        observerBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        observerBadge.setForeground(new Color(67, 56, 202));

        notificationLabel = new JLabel("System ready. Welcome to TradeForge Design Pattern Lab!");
        notificationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationLabel.setForeground(new Color(30, 27, 75));

        bannerPanel.add(observerBadge, BorderLayout.WEST);
        bannerPanel.add(notificationLabel, BorderLayout.CENTER);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerBar, BorderLayout.NORTH);
        topContainer.add(bannerPanel, BorderLayout.SOUTH);
        rootPanel.add(topContainer, BorderLayout.NORTH);

        // 3. CENTER TABBED PANE
        stocksPanel = new StocksPanel();
        portfolioPanel = new PortfolioPanel();
        tradePanel = new TradePanel();
        historyPanel = new TransactionHistoryPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("  📈 Market Stocks  ", stocksPanel);
        tabbedPane.addTab("  💼 My Portfolio  ", portfolioPanel);
        tabbedPane.addTab("  ⚡ Trading Desk  ", tradePanel);
        tabbedPane.addTab("  📜 Transaction History  ", historyPanel);

        rootPanel.add(tabbedPane, BorderLayout.CENTER);

        // 4. FOOTER STATUS BAR
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(241, 245, 249));
        statusBar.setBorder(new EmptyBorder(6, 16, 6, 16));

        JLabel patternsLabel = new JLabel("Design Patterns Active: Singleton • Factory • Proxy • Chain of Resp • Observer • Builder • Mediator • Template • Memento • Prototype");
        patternsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        patternsLabel.setForeground(new Color(100, 116, 139));

        JLabel dbStatus = new JLabel("● MySQL JDBC Connected");
        dbStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dbStatus.setForeground(new Color(16, 185, 129));

        statusBar.add(patternsLabel, BorderLayout.WEST);
        statusBar.add(dbStatus, BorderLayout.EAST);
        rootPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    public void updateUser(User user) {
        if (user != null) {
            usernameLabel.setText("Logged in as: " + user.getUsername() + " (" + user.getAccountType() + ")");
            balanceLabel.setText(String.format("Available Balance: ₹%.2f", user.getBalance()));
        }
    }

    public void setNotification(String message) {
        notificationLabel.setText(message);
    }

    public void switchToTradeTab() {
        tabbedPane.setSelectedIndex(2); // Switch to Trade Desk tab
    }

    public StocksPanel getStocksPanel() {
        return stocksPanel;
    }

    public PortfolioPanel getPortfolioPanel() {
        return portfolioPanel;
    }

    public TradePanel getTradePanel() {
        return tradePanel;
    }

    public TransactionHistoryPanel getHistoryPanel() {
        return historyPanel;
    }

    public void addRefreshListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
}
