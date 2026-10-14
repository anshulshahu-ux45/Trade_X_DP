package com.tradeforge.template;

import com.tradeforge.chain.TradingRequest;
import com.tradeforge.dao.PortfolioDAO;
import com.tradeforge.dao.StockDAO;
import com.tradeforge.dao.UserDAO;
import com.tradeforge.factory.Transaction;
import com.tradeforge.factory.TransactionFactory;
import com.tradeforge.model.Agent;
import com.tradeforge.model.Portfolio;
import com.tradeforge.model.Stock;
import com.tradeforge.model.User;
import com.tradeforge.observer.StockTradingSubject;
import java.sql.Connection;

/**
 * Concrete Workflow for SELL operations in the Template Method Pattern.
 */
public class SellTradingWorkflow extends TradingTemplate {

    public SellTradingWorkflow(StockTradingSubject subject) {
        super(subject);
    }

    @Override
    protected void checkAvailability(TradingRequest request, Connection connection) throws Exception {
        Portfolio holding = new PortfolioDAO().findHolding(request.getUserId(), request.getStockId(), connection);

        if (holding == null || holding.getQuantity() < request.getQuantity()) {
            int owned = holding != null ? holding.getQuantity() : 0;
            throw new IllegalArgumentException(String.format("You do not own enough shares to sell. Owned: %d, Requested: %d",
                    owned, request.getQuantity()));
        }
    }

    @Override
    protected double executeOperation(TradingRequest request, Agent agent, Connection connection) throws Exception {
        User user = new UserDAO().findById(request.getUserId(), connection);
        Stock stock = new StockDAO().findById(request.getStockId(), connection);

        Transaction transaction = TransactionFactory.create("SELL");
        double netPayout = transaction.calculateTotal(stock.getPrice(), request.getQuantity());

        // 1. Credit proceeds to user balance
        double updatedBalance = user.getBalance() + netPayout;
        new UserDAO().updateBalance(user.getId(), updatedBalance, connection);

        // 2. Return sold shares back to market stock inventory
        int updatedStockQty = stock.getQuantity() + request.getQuantity();
        new StockDAO().updateQuantity(stock.getId(), updatedStockQty, connection);

        // 3. Deduct sold shares from user portfolio (-quantity)
        new PortfolioDAO().changeHolding(user.getId(), stock.getId(), -request.getQuantity(), connection);

        return netPayout;
    }
}
