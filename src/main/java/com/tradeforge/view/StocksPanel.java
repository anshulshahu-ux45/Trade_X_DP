package com.tradeforge.view;

import com.tradeforge.model.Stock;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * VIEW: Stocks Panel
 * Displays available stocks in the market catalog with live selection.
 */
public class StocksPanel extends JPanel {

    private JTable stocksTable;
    private DefaultTableModel tableModel;
    private JButton selectButton;
    private List<Stock> currentStocks;
    private Consumer<Stock> stockSelectListener;

    public StocksPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        // Title header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Market Stock Catalog");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));
        topPanel.add(title, BorderLayout.WEST);

        selectButton = new JButton("Select Stock for Trade");
        selectButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selectButton.setBackground(new Color(37, 99, 235));
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(selectButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Symbol", "Company Name", "Price (₹)", "Available Qty"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stocksTable = new JTable(tableModel);
        stocksTable.setRowHeight(28);
        stocksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stocksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        stocksTable.getTableHeader().setBackground(new Color(241, 245, 249));
        stocksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click listener
        stocksTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    triggerSelect();
                }
            }
        });

        selectButton.addActionListener(e -> triggerSelect());

        JScrollPane scrollPane = new JScrollPane(stocksTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void triggerSelect() {
        int selectedRow = stocksTable.getSelectedRow();
        if (selectedRow >= 0 && currentStocks != null && selectedRow < currentStocks.size()) {
            Stock selected = currentStocks.get(selectedRow);
            if (stockSelectListener != null) {
                stockSelectListener.accept(selected);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a stock from the table.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setStockSelectListener(Consumer<Stock> listener) {
        this.stockSelectListener = listener;
    }

    public void updateStocks(List<Stock> stocks) {
        this.currentStocks = stocks;
        tableModel.setRowCount(0);
        if (stocks != null) {
            for (Stock s : stocks) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getSymbol(),
                        s.getCompanyName(),
                        String.format("₹%.2f", s.getPrice()),
                        s.getQuantity()
                });
            }
        }
    }

    public Stock getSelectedStock() {
        int row = stocksTable.getSelectedRow();
        if (row >= 0 && currentStocks != null && row < currentStocks.size()) {
            return currentStocks.get(row);
        }
        return null;
    }
}
