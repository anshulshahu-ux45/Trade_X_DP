package com.tradeforge.view;

import com.tradeforge.model.TransactionRecord;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * VIEW: Transaction History Panel
 * Displays completed transactions audit log including the assigned agent from the CoR chain.
 */
public class TransactionHistoryPanel extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton duplicateButton;
    private List<TransactionRecord> currentRecords;
    private Consumer<TransactionRecord> duplicateListener;

    public TransactionHistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        // Top header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Completed Transactions Audit Log");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(30, 41, 59));
        topPanel.add(title, BorderLayout.WEST);

        duplicateButton = new JButton("Duplicate Selected Trade (Prototype)");
        duplicateButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        duplicateButton.setBackground(new Color(99, 102, 241));
        duplicateButton.setForeground(Color.WHITE);
        duplicateButton.setFocusPainted(false);
        duplicateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        duplicateButton.addActionListener(e -> triggerDuplicate());
        topPanel.add(duplicateButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Tx ID", "Stock", "Type", "Quantity", "Price (₹)", "Fee (₹)", "Total (₹)", "Assigned Broker Agent", "Date & Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(28);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(241, 245, 249));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void triggerDuplicate() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0 && currentRecords != null && selectedRow < currentRecords.size()) {
            TransactionRecord record = currentRecords.get(selectedRow);
            if (duplicateListener != null) {
                duplicateListener.accept(record);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a transaction to duplicate.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setDuplicateListener(Consumer<TransactionRecord> listener) {
        this.duplicateListener = listener;
    }

    public void updateHistory(List<TransactionRecord> records) {
        this.currentRecords = records;
        tableModel.setRowCount(0);
        if (records != null) {
            for (TransactionRecord r : records) {
                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getSymbol(),
                        r.getTransactionType(),
                        r.getQuantity(),
                        String.format("₹%.2f", r.getPrice()),
                        String.format("₹%.2f", r.getCharge()),
                        String.format("₹%.2f", r.getTotalAmount()),
                        r.getAgentName(),
                        r.getTransactionDate() != null ? r.getTransactionDate().toString() : "N/A"
                });
            }
        }
    }
}
