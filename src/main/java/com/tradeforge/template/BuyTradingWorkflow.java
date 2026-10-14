package com.tradeforge.template;

import com.tradeforge.chain.TradingRequest;
import com.tradeforge.dao.PortfolioDAO;
import com.tradeforge.dao.StockDAO;
import com.tradeforge.dao.UserDAO;
import com.tradeforge.factory.Transaction;
import com.tradeforge.factory.TransactionFactory;
import com.tradeforge.model.Agent;
import com.tradeforge.model.Stock;
import com.tradeforge.model.User;
import com.tradeforge.observer.StockTradingSubject;
import java.sql.Connection;

/**
 * Concrete Workflow for BUY operations in the Template Method Pattern.
 */
public class BuyTradingWorkflow extends TradingTemplate {

    public BuyTradingWorkflow(StockTradingSubject subject) {
        super(subject);
    }

    @Override
    protected void checkAvailability(TradingRequest request, Connection connection) throws Exception {
        User user = new UserDAO().findById(request.getUserId(), connection);
        Stock stock = new StockDAO().findById(request.getStockId(), connection);

        if (stock.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(String.format("Insufficient stock available in market. Available: %d, Requested: %d",
                    stock.getQuantity(), request.getQuantity()));
        }

        Transaction transaction = TransactionFactory.create("BUY");
        double totalRequired = transaction.calculateTotal(stock.getPrice(), request.getQuantity());

        if (user.getBalance() < totalRequired) {
            throw new IllegalArgumentException(String.format("Insufficient balance to buy. Total required: ₹%.2f, Available balance: ₹%.2f",
                    totalRequired, user.getBalance()));
        }
    }

    @Override
    protected double executeOperation(TradingRequest request, Agent agent, Connection connection) throws Exception {
        User user = new UserDAO().findById(request.getUserId(), connection);
        Stock stock = new StockDAO().findById(request.getStockId(), connection);

        Transaction transaction = TransactionFactory.create("BUY");
        double totalToDeduct = transaction.calculateTotal(stock.getPrice(), request.getQuantity());

        // 1. Deduct funds from user balance
        double updatedBalance = user.getBalance() - totalToDeduct;
        new UserDAO().updateBalance(user.getId(), updatedBalance, connection);

        // 2. Reduce available stock inventory in the market
        int updatedStockQty = stock.getQuantity() - request.getQuantity();
        new StockDAO().updateQuantity(stock.getId(), updatedStockQty, connection);

        // 3. Credit purchased shares to user's portfolio (+quantity)
        new PortfolioDAO().changeHolding(user.getId(), stock.getId(), request.getQuantity(), connection);

        return totalToDeduct;
    }
}
