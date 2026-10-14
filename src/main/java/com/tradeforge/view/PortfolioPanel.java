package com.tradeforge.view;

import com.tradeforge.model.Portfolio;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * VIEW: Portfolio Panel
 * Displays stocks currently owned by the logged-in user.
 */
public class PortfolioPanel extends JPanel {

    private JTable portfolioTable;
    private DefaultTableModel tableModel;
    private JButton sellButton;
    private List<Portfolio> currentPortfolio;
    private Consumer<Portfolio> sellSelectListener;

    public PortfolioPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        // Top header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Your Stock Portfolio");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));
        topPanel.add(title, BorderLayout.WEST);

        sellButton = new JButton("Sell Selected Holding");
        sellButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sellButton.setBackground(new Color(220, 38, 38));
        sellButton.setForeground(Color.WHITE);
        sellButton.setFocusPainted(false);
        sellButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(sellButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Stock ID", "Symbol", "Company Name", "Shares Owned", "Current Price (₹)", "Total Value (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        portfolioTable = new JTable(tableModel);
        portfolioTable.setRowHeight(28);
        portfolioTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        portfolioTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        portfolioTable.getTableHeader().setBackground(new Color(241, 245, 249));
        portfolioTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double click listener
        portfolioTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    triggerSell();
                }
            }
        });

        sellButton.addActionListener(e -> triggerSell());

        JScrollPane scrollPane = new JScrollPane(portfolioTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void triggerSell() {
        int selectedRow = portfolioTable.getSelectedRow();
        if (selectedRow >= 0 && currentPortfolio != null && selectedRow < currentPortfolio.size()) {
            Portfolio selected = currentPortfolio.get(selectedRow);
            if (sellSelectListener != null) {
                sellSelectListener.accept(selected);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a holding from your portfolio.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setSellSelectListener(Consumer<Portfolio> listener) {
        this.sellSelectListener = listener;
    }

    public void updatePortfolio(List<Portfolio> holdings) {
        this.currentPortfolio = holdings;
        tableModel.setRowCount(0);
        if (holdings != null) {
            for (Portfolio p : holdings) {
                double totalValue = p.getQuantity() * p.getCurrentPrice();
                tableModel.addRow(new Object[]{
                        p.getStockId(),
                        p.getSymbol(),
                        p.getCompanyName(),
                        p.getQuantity(),
                        String.format("₹%.2f", p.getCurrentPrice()),
                        String.format("₹%.2f", totalValue)
                });
            }
        }
    }

    public Portfolio getSelectedHolding() {
        int row = portfolioTable.getSelectedRow();
        if (row >= 0 && currentPortfolio != null && row < currentPortfolio.size()) {
            return currentPortfolio.get(row);
        }
        return null;
    }
}
