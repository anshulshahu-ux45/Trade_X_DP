package com.tradeforge.controller;

import com.tradeforge.builder.TradeBuilder;
import com.tradeforge.chain.TradingRequest;
import com.tradeforge.mediator.TradingMediator;
import com.tradeforge.model.Portfolio;
import com.tradeforge.model.Stock;
import com.tradeforge.model.TransactionRecord;
import com.tradeforge.model.User;
import com.tradeforge.template.TradeResult;
import com.tradeforge.view.DashboardFrame;
import com.tradeforge.view.LoginFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * CONTROLLER: DashboardController
 * Coordinates all desktop application events between the DashboardFrame and the TradingMediator.
 * Adheres strictly to MVC: does not run SQL queries directly.
 */
public class DashboardController {

    private final DashboardFrame view;
    private final TradingMediator mediator;
    private User currentUser;

    public DashboardController(DashboardFrame view, TradingMediator mediator, User currentUser) {
        this.view = view;
        this.mediator = mediator;
        this.currentUser = currentUser;

        initController();
        loadAllData();
    }

    private void initController() {
        // 1. Register Observer listener so UI receives real-time trade event updates
        mediator.registerNotificationListener(message -> {
            SwingUtilities.invokeLater(() -> view.setNotification(message));
        });

        // 2. Configure TradePanel dynamic agent resolver (Chain of Responsibility)
        view.getTradePanel().setAgentResolver(mediator::determineAgent);

        // 3. Configure TradePanel order execution (Builder Pattern -> Mediator)
        view.getTradePanel().setExecuteListener(this::handleExecuteTrade);

        // 4. Configure selection from StocksPanel
        view.getStocksPanel().setStockSelectListener(this::handleSelectStockFromMarket);

        // 5. Configure quick-sell from PortfolioPanel
        view.getPortfolioPanel().setSellSelectListener(this::handleSellFromPortfolio);

        // 6. Configure duplicate trade from TransactionHistoryPanel (Prototype Pattern)
        view.getHistoryPanel().setDuplicateListener(this::handleDuplicateFromHistory);

        // 7. Configure header actions
        view.addRefreshListener(e -> loadAllData());
        view.addLogoutListener(this::handleLogout);
    }

    /**
     * Loads/refreshes user balance, market stock catalog, portfolio holdings, and transaction history.
     */
    public void loadAllData() {
        try {
            // Refresh user details
            currentUser = mediator.getUser(currentUser.getId());
            view.updateUser(currentUser);

            // Refresh stocks
            List<Stock> stocks = mediator.getAllStocks();
            view.getStocksPanel().updateStocks(stocks);
            view.getTradePanel().setStocks(stocks);

            // Refresh portfolio
            List<Portfolio> portfolio = mediator.getUserPortfolio(currentUser.getId());
            view.getPortfolioPanel().updatePortfolio(portfolio);

            // Refresh transactions history
            List<TransactionRecord> transactions = mediator.getUserTransactions(currentUser.getId());
            view.getHistoryPanel().updateHistory(transactions);

            view.getTradePanel().setStatusMessage("Market data updated successfully.", false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error loading market data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles order execution using the BUILDER PATTERN and MEDIATOR.
     */
    private void handleExecuteTrade(TradeBuilder builder) {
        try {
            // Complete building the Trade Request with authenticated user ID
            TradingRequest request = builder
                    .setUserId(currentUser.getId())
                    .build();

            // Execute trade via the central Mediator
            TradeResult result = mediator.executeTrade(request, currentUser.getId());

            // Success feedback
            String successMsg = String.format("<html><b>Trade Executed Successfully!</b><br>"
                    + "• Transaction Type: %s<br>"
                    + "• Quantity: %d shares<br>"
                    + "• Total Amount: ₹%.2f<br>"
                    + "• Assigned Broker Agent: %s</html>",
                    request.getType(), request.getQuantity(), result.getTotalAmount(),
                    result.getAssignedAgent() != null ? result.getAssignedAgent().getName() : "Assigned");

            JOptionPane.showMessageDialog(view, successMsg, "Trade Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh all application views
            loadAllData();
            view.getTradePanel().setStatusMessage("Order completed: " + result.getMessage(), false);

        } catch (IllegalArgumentException | SecurityException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Order Rejected", JOptionPane.WARNING_MESSAGE);
            view.getTradePanel().setStatusMessage(ex.getMessage(), true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Transaction failed: " + ex.getMessage(), "Execution Error", JOptionPane.ERROR_MESSAGE);
            view.getTradePanel().setStatusMessage("Failed: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    /**
     * User clicked "Select Stock for Trade" in StocksPanel.
     */
    private void handleSelectStockFromMarket(Stock stock) {
        view.getTradePanel().setSelectedStock(stock.getId());
        view.getTradePanel().setTransactionType("BUY");
        view.switchToTradeTab();
    }

    /**
     * User clicked "Sell Selected Holding" in PortfolioPanel.
     */
    private void handleSellFromPortfolio(Portfolio holding) {
        view.getTradePanel().setSelectedStock(holding.getStockId());
        view.getTradePanel().setTransactionType("SELL");
        view.getTradePanel().setQuantity(holding.getQuantity());
        view.switchToTradeTab();
    }

    /**
     * User clicked "Duplicate Selected Trade" in TransactionHistoryPanel (Prototype Pattern).
     */
    private void handleDuplicateFromHistory(TransactionRecord record) {
        // Find stock by symbol in current stock catalog
        try {
            List<Stock> stocks = mediator.getAllStocks();
            for (Stock s : stocks) {
                if (s.getSymbol().equalsIgnoreCase(record.getSymbol())) {
                    view.getTradePanel().setSelectedStock(s.getId());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        view.getTradePanel().setTransactionType(record.getTransactionType());
        view.getTradePanel().setQuantity(record.getQuantity());
        view.switchToTradeTab();

        JOptionPane.showMessageDialog(view,
                "Prototype Pattern Applied!\n\n" +
                "Successfully cloned values from Transaction #" + record.getId() + " into Trade Desk:\n" +
                "• Stock: " + record.getSymbol() + "\n" +
                "• Type: " + record.getTransactionType() + "\n" +
                "• Quantity: " + record.getQuantity() + " shares\n\n" +
                "The draft is ready for editing and has not been executed yet.",
                "Duplicate Trade (Prototype)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLogout(ActionEvent event) {
        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to log out of TradeForge?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            view.setVisible(false);
            view.dispose();

            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame, mediator);
            loginFrame.setVisible(true);
        }
    }
}
