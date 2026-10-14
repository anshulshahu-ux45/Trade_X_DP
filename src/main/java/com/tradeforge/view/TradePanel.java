package com.tradeforge.view;

import com.tradeforge.builder.TradeBuilder;
import com.tradeforge.chain.TradingRequest;
import com.tradeforge.factory.Transaction;
import com.tradeforge.factory.TransactionFactory;
import com.tradeforge.memento.TradeCaretaker;
import com.tradeforge.memento.TradeMemento;
import com.tradeforge.model.Agent;
import com.tradeforge.model.Stock;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * VIEW: Trade Panel (Trading Desk)
 * 
 * Interactive control center genuinely demonstrating:
 * 1. BUILDER PATTERN: Constructs trade orders via TradeBuilder.
 * 2. CHAIN OF RESPONSIBILITY: Live preview of assigned Agent 1, 2, or 3 based on quantity.
 * 3. FACTORY PATTERN: Live recalculation of BUY/SELL fees.
 * 4. PROTOTYPE PATTERN: "Duplicate Trade" clones request values into form.
 * 5. MEMENTO PATTERN: "Save Draft State" and "Undo / Restore State" buttons.
 */
public class TradePanel extends JPanel {

    // Components
    private JComboBox<StockComboItem> stockComboBox;
    private JRadioButton buyRadio;
    private JRadioButton sellRadio;
    private ButtonGroup typeGroup;
    private JSpinner quantitySpinner;

    // Live display cards
    private JLabel priceLabel;
    private JLabel baseLabel;
    private JLabel chargeLabel;
    private JLabel totalLabel;
    private JLabel agentBadge;
    private JLabel statusLabel;

    // Demonstration action buttons
    private JButton executeButton;
    private JButton duplicateButton;
    private JButton saveDraftButton;
    private JButton undoDraftButton;

    // Memento Caretaker
    private final TradeCaretaker caretaker = new TradeCaretaker();

    // Callbacks
    private Consumer<TradeBuilder> executeListener;
    private Function<Integer, Agent> agentResolver;

    public static class StockComboItem {
        private final Stock stock;

        public StockComboItem(Stock stock) {
            this.stock = stock;
        }

        public Stock getStock() {
            return stock;
        }

        @Override
        public String toString() {
            return String.format("%s - %s (₹%.2f | Stock: %d)",
                    stock.getSymbol(), stock.getCompanyName(), stock.getPrice(), stock.getQuantity());
        }
    }

    public TradePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        // North: Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Trade Execution Desk");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 41, 59));
        titlePanel.add(title, BorderLayout.WEST);

        statusLabel = new JLabel("Configure your order below.", SwingConstants.RIGHT);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(71, 85, 105));
        titlePanel.add(statusLabel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // Center: Form and Live Calculations
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Left Box: Inputs
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(203, 213, 225)), " Order Configuration ", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Stock Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblStock = new JLabel("Select Stock:");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputPanel.add(lblStock, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        stockComboBox = new JComboBox<>();
        stockComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stockComboBox.addActionListener(e -> recalculateSummary());
        inputPanel.add(stockComboBox, gbc);

        // Order Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblType = new JLabel("Order Type:");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputPanel.add(lblType, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        radioPanel.setOpaque(false);
        buyRadio = new JRadioButton("BUY (Purchase)", true);
        buyRadio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        buyRadio.setForeground(new Color(22, 101, 52));
        buyRadio.setOpaque(false);

        sellRadio = new JRadioButton("SELL (Liquidate)");
        sellRadio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sellRadio.setForeground(new Color(153, 27, 27));
        sellRadio.setOpaque(false);

        typeGroup = new ButtonGroup();
        typeGroup.add(buyRadio);
        typeGroup.add(sellRadio);

        buyRadio.addActionListener(e -> recalculateSummary());
        sellRadio.addActionListener(e -> recalculateSummary());

        radioPanel.add(buyRadio);
        radioPanel.add(sellRadio);
        inputPanel.add(radioPanel, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblQty = new JLabel("Quantity (Shares):");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputPanel.add(lblQty, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100000, 1));
        quantitySpinner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        quantitySpinner.addChangeListener(e -> recalculateSummary());
        inputPanel.add(quantitySpinner, gbc);

        // Pattern Actions toolbar inside left panel
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel patternToolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        patternToolPanel.setOpaque(false);

        duplicateButton = new JButton("Duplicate Trade (Prototype)");
        duplicateButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        duplicateButton.setToolTipText("Prototype Pattern: Clones current trade request values into an independent editable draft");
        duplicateButton.addActionListener(e -> handleDuplicateTrade());

        saveDraftButton = new JButton("Save Draft (Memento)");
        saveDraftButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        saveDraftButton.setToolTipText("Memento Pattern: Captures and saves current form inputs into a memento snapshot");
        saveDraftButton.addActionListener(e -> handleSaveMemento());

        undoDraftButton = new JButton("Undo / Restore (Memento)");
        undoDraftButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        undoDraftButton.setToolTipText("Memento Pattern: Restores previous form values from the saved memento snapshot");
        undoDraftButton.addActionListener(e -> handleUndoMemento());

        patternToolPanel.add(duplicateButton);
        patternToolPanel.add(saveDraftButton);
        patternToolPanel.add(undoDraftButton);
        inputPanel.add(patternToolPanel, gbc);

        centerPanel.add(inputPanel);

        // Right Box: Live Calculations & Assigned Agent (CoR)
        JPanel summaryPanel = new JPanel(new GridLayout(6, 1, 6, 6));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(203, 213, 225)), " Order Summary & Agent Assignment ", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(51, 65, 85)),
                new EmptyBorder(12, 16, 12, 16)
        ));

        priceLabel = createMetricLabel("Stock Unit Price:", "₹0.00");
        baseLabel = createMetricLabel("Base Order Value:", "₹0.00");
        chargeLabel = createMetricLabel("Brokerage Fee (2%):", "₹0.00");
        totalLabel = createMetricLabel("Estimated Total Amount:", "₹0.00");

        // Chain of Responsibility Agent Badge
        JPanel agentPanel = new JPanel(new BorderLayout(5, 2));
        agentPanel.setOpaque(false);
        JLabel agentTitle = new JLabel("Assigned Agent (CoR Chain):");
        agentTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        agentTitle.setForeground(new Color(15, 23, 42));

        agentBadge = new JLabel("Agent 1 - Rahul Sharma (Tier 1: 1-500)", SwingConstants.CENTER);
        agentBadge.setOpaque(true);
        agentBadge.setBackground(new Color(224, 231, 255));
        agentBadge.setForeground(new Color(49, 46, 129));
        agentBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        agentBadge.setBorder(new LineBorder(new Color(199, 210, 254), 1, true));
        agentBadge.setPreferredSize(new Dimension(100, 28));

        agentPanel.add(agentTitle, BorderLayout.NORTH);
        agentPanel.add(agentBadge, BorderLayout.CENTER);

        summaryPanel.add(priceLabel);
        summaryPanel.add(baseLabel);
        summaryPanel.add(chargeLabel);
        summaryPanel.add(totalLabel);
        summaryPanel.add(agentPanel);

        centerPanel.add(summaryPanel);
        add(centerPanel, BorderLayout.CENTER);

        // South: Execute Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        executeButton = new JButton("Execute Order via TradeBuilder & Mediator");
        executeButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        executeButton.setBackground(new Color(16, 185, 129));
        executeButton.setForeground(Color.WHITE);
        executeButton.setFocusPainted(false);
        executeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        executeButton.setPreferredSize(new Dimension(200, 44));
        executeButton.addActionListener(e -> handleExecute());

        bottomPanel.add(executeButton, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createMetricLabel(String title, String initialValue) {
        JLabel label = new JLabel(String.format("<html><b>%s</b> <font color='#2563EB'>%s</font></html>", title, initialValue));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    public void setStocks(List<Stock> stocks) {
        Stock selectedStock = getSelectedStock();
        stockComboBox.removeAllItems();
        if (stocks != null) {
            for (Stock s : stocks) {
                stockComboBox.addItem(new StockComboItem(s));
            }
        }
        if (selectedStock != null) {
            setSelectedStock(selectedStock.getId());
        }
        recalculateSummary();
    }

    public void setSelectedStock(int stockId) {
        for (int i = 0; i < stockComboBox.getItemCount(); i++) {
            StockComboItem item = stockComboBox.getItemAt(i);
            if (item.getStock().getId() == stockId) {
                stockComboBox.setSelectedIndex(i);
                break;
            }
        }
        recalculateSummary();
    }

    public void setTransactionType(String type) {
        if ("SELL".equalsIgnoreCase(type)) {
            sellRadio.setSelected(true);
        } else {
            buyRadio.setSelected(true);
        }
        recalculateSummary();
    }

    public void setQuantity(int quantity) {
        quantitySpinner.setValue(Math.max(1, quantity));
        recalculateSummary();
    }

    public Stock getSelectedStock() {
        StockComboItem item = (StockComboItem) stockComboBox.getSelectedItem();
        return item != null ? item.getStock() : null;
    }

    public String getTransactionType() {
        return buyRadio.isSelected() ? "BUY" : "SELL";
    }

    public int getQuantity() {
        return (Integer) quantitySpinner.getValue();
    }

    public void setAgentResolver(Function<Integer, Agent> resolver) {
        this.agentResolver = resolver;
        recalculateSummary();
    }

    public void setExecuteListener(Consumer<TradeBuilder> listener) {
        this.executeListener = listener;
    }

    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? new Color(220, 38, 38) : new Color(16, 185, 129));
    }

    public void recalculateSummary() {
        Stock stock = getSelectedStock();
        int quantity = getQuantity();
        String type = getTransactionType();

        if (stock == null) {
            priceLabel.setText("<html><b>Stock Unit Price:</b> <font color='#2563EB'>₹0.00</font></html>");
            baseLabel.setText("<html><b>Base Order Value:</b> <font color='#2563EB'>₹0.00</font></html>");
            chargeLabel.setText("<html><b>Brokerage Fee (2%):</b> <font color='#2563EB'>₹0.00</font></html>");
            totalLabel.setText("<html><b>Estimated Total Amount:</b> <font color='#2563EB'>₹0.00</font></html>");
            return;
        }

        Transaction tx = TransactionFactory.create(type);
        double price = stock.getPrice();
        double base = price * quantity;
        double charge = tx.calculateCharge(price, quantity);
        double total = tx.calculateTotal(price, quantity);

        priceLabel.setText(String.format("<html><b>Stock Unit Price:</b> <font color='#2563EB'>₹%.2f</font></html>", price));
        baseLabel.setText(String.format("<html><b>Base Order Value:</b> <font color='#2563EB'>₹%.2f</font></html>", base));
        chargeLabel.setText(String.format("<html><b>Brokerage Fee (2%%):</b> <font color='#2563EB'>₹%.2f</font></html>", charge));
        totalLabel.setText(String.format("<html><b>Estimated Total:</b> <font color='#16A34A'><b>₹%.2f</b></font> (%s)</html>",
                total, type.equals("BUY") ? "to pay" : "to receive"));

        // Chain of Responsibility Agent resolution
        if (agentResolver != null) {
            Agent agent = agentResolver.apply(quantity);
            if (agent != null) {
                agentBadge.setText(agent.getName());
                if (quantity <= 500) {
                    agentBadge.setBackground(new Color(224, 231, 255)); // Light Blue (Tier 1)
                    agentBadge.setForeground(new Color(49, 46, 129));
                } else if (quantity <= 700) {
                    agentBadge.setBackground(new Color(254, 243, 199)); // Light Yellow (Tier 2)
                    agentBadge.setForeground(new Color(146, 64, 14));
                } else {
                    agentBadge.setBackground(new Color(254, 226, 226)); // Light Red (Tier 3)
                    agentBadge.setForeground(new Color(153, 27, 27));
                }
            }
        }
    }

    private void handleExecute() {
        Stock stock = getSelectedStock();
        if (stock == null) {
            JOptionPane.showMessageDialog(this, "Please select a stock first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty = getQuantity();
        String type = getTransactionType();

        // BUILDER PATTERN: Constructing TradeBuilder
        TradeBuilder builder = new TradeBuilder()
                .setStockId(stock.getId())
                .setType(type)
                .setQuantity(qty);

        if (executeListener != null) {
            executeListener.accept(builder);
        }
    }

    /**
     * PROTOTYPE PATTERN: Clones current form request into an independent copy.
     */
    private void handleDuplicateTrade() {
        Stock stock = getSelectedStock();
        if (stock == null) {
            JOptionPane.showMessageDialog(this, "Please select a stock to duplicate.", "No Stock", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create original request
        TradingRequest original = new TradingRequest(1, stock.getId(), getQuantity(), getTransactionType());

        // Clone via Prototype pattern
        TradingRequest cloned = original.cloneTrade();

        // Populate back to verify deep copy
        setSelectedStock(cloned.getStockId());
        setTransactionType(cloned.getType());
        setQuantity(cloned.getQuantity());

        setStatusMessage("Prototype Pattern: Trade request duplicated successfully into draft form.", false);
        JOptionPane.showMessageDialog(this,
                "Prototype Pattern Applied!\n\n" +
                "Successfully cloned Trade Request:\n" +
                "• Stock: " + stock.getSymbol() + "\n" +
                "• Type: " + cloned.getType() + "\n" +
                "• Quantity: " + cloned.getQuantity() + "\n\n" +
                "The cloned object is an independent editable copy and has NOT executed any transaction yet.",
                "Duplicate Trade (Prototype Pattern)", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * MEMENTO PATTERN: Saves current state to Caretaker.
     */
    private void handleSaveMemento() {
        Stock stock = getSelectedStock();
        if (stock == null) {
            JOptionPane.showMessageDialog(this, "Please select a stock before saving draft.", "No Stock", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        TradeMemento memento = new TradeMemento(stock.getId(), getTransactionType(), getQuantity());
        caretaker.save(memento);

        setStatusMessage("Memento Pattern: State saved at " + memento.getTimestamp(), false);
        JOptionPane.showMessageDialog(this,
                "Memento Pattern Applied!\n\n" +
                "Snapshot saved successfully:\n" +
                "• Saved at: " + memento.getTimestamp() + "\n" +
                "• Stock ID: " + memento.getStockId() + " (" + stock.getSymbol() + ")\n" +
                "• Type: " + memento.getType() + "\n" +
                "• Quantity: " + memento.getQuantity() + "\n\n" +
                "You can now change values and click 'Undo / Restore' to revert.",
                "Save State (Memento Pattern)", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * MEMENTO PATTERN: Restores previous state from Caretaker.
     */
    private void handleUndoMemento() {
        if (!caretaker.hasSavedState()) {
            JOptionPane.showMessageDialog(this, "No saved draft state found to restore.", "Memento History Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TradeMemento memento = caretaker.undo();
        if (memento != null) {
            setSelectedStock(memento.getStockId());
            setTransactionType(memento.getType());
            setQuantity(memento.getQuantity());

            setStatusMessage("Memento Pattern: Restored state from " + memento.getTimestamp(), false);
            JOptionPane.showMessageDialog(this,
                    "Memento Pattern Applied!\n\n" +
                    "Restored previous form state:\n" +
                    "• Saved at: " + memento.getTimestamp() + "\n" +
                    "• Stock ID: " + memento.getStockId() + "\n" +
                    "• Type: " + memento.getType() + "\n" +
                    "• Quantity: " + memento.getQuantity(),
                    "Undo / Restore (Memento Pattern)", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
