package com.tradeforge.template;

import com.tradeforge.chain.BalanceValidationHandler;
import com.tradeforge.chain.QuantityValidationHandler;
import com.tradeforge.chain.StockValidationHandler;
import com.tradeforge.chain.TradingRequest;
import com.tradeforge.chain.UserValidationHandler;
import com.tradeforge.chain.ValidationHandler;
import com.tradeforge.dao.StockDAO;
import com.tradeforge.dao.TransactionDAO;
import com.tradeforge.factory.Transaction;
import com.tradeforge.factory.TransactionFactory;
import com.tradeforge.model.Agent;
import com.tradeforge.model.Stock;
import com.tradeforge.observer.StockTradingSubject;
import java.sql.Connection;

/**
 * DESIGN PATTERN 8: TEMPLATE METHOD PATTERN (Abstract Template)
 * 
 * Defines the fixed skeleton algorithm for executing a stock trade in TradeForge:
 * 1. Validate the trade request using the Chain of Responsibility.
 * 2. Check the relevant balance or stock availability (implemented by concrete subclasses).
 * 3. Execute the BUY or SELL operation (implemented by concrete subclasses).
 * 4. Save the transaction record into the database.
 * 5. Notify observers after successful trade execution.
 * 
 * The Template Method 'processTrade' is declared 'final' so subclasses cannot
 * override the execution sequence, while customizing specific steps.
 */
public abstract class TradingTemplate {

    protected final StockTradingSubject subject;

    public TradingTemplate(StockTradingSubject subject) {
        this.subject = subject;
    }

    /**
     * THE TEMPLATE METHOD
     * Enforces the immutable sequence of steps for executing any trade.
     */
    public final TradeResult processTrade(TradingRequest request, Agent agent, Connection connection) throws Exception {
        // Step 1: Validate the request using the Chain of Responsibility
        validateRequest(request, connection);

        // Step 2: Check balance (for BUY) or stock availability (for SELL)
        checkAvailability(request, connection);

        // Step 3: Execute BUY/SELL business logic and update balances
        double totalAmount = executeOperation(request, agent, connection);

        // Step 4: Save the transaction audit record to the database
        saveTransaction(request, agent, totalAmount, connection);

        // Step 5: Notify registered Observers of the successful trade
        notifySuccess(request, agent, totalAmount, connection);

        return new TradeResult(true, "Trade executed successfully!", totalAmount, agent);
    }

    /**
     * Step 1 (Common): Validate using Chain of Responsibility.
     */
    protected void validateRequest(TradingRequest request, Connection connection) throws Exception {
        ValidationHandler chain = new UserValidationHandler();
        chain.setNext(new StockValidationHandler())
             .setNext(new BalanceValidationHandler())
             .setNext(new QuantityValidationHandler());

        if (!chain.validate(request, connection)) {
            String errorMsg = request.getError() != null ? request.getError() : "Trade validation failed.";
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * Step 2 (Abstract/Hook): Subclasses implement specific availability checks.
     */
    protected abstract void checkAvailability(TradingRequest request, Connection connection) throws Exception;

    /**
     * Step 3 (Abstract/Hook): Subclasses implement specific BUY or SELL math and persistence.
     */
    protected abstract double executeOperation(TradingRequest request, Agent agent, Connection connection) throws Exception;

    /**
     * Step 4 (Common): Save the transaction into the MySQL database.
     */
    protected void saveTransaction(TradingRequest request, Agent agent, double totalAmount, Connection connection) throws Exception {
        Stock stock = new StockDAO().findById(request.getStockId(), connection);
        Transaction tx = TransactionFactory.create(request.getType());
        double charge = tx.calculateCharge(stock.getPrice(), request.getQuantity());

        new TransactionDAO().insert(
                request.getUserId(),
                stock.getId(),
                agent.getId(),
                request.getType(),
                request.getQuantity(),
                stock.getPrice(),
                charge,
                totalAmount,
                connection
        );
    }

    /**
     * Step 5 (Common): Notify all registered Observers.
     */
    protected void notifySuccess(TradingRequest request, Agent agent, double totalAmount, Connection connection) throws Exception {
        if (subject != null) {
            Stock stock = new StockDAO().findById(request.getStockId(), connection);
            String message = String.format("%s trade for %d shares of %s (₹%.2f) processed by %s.",
                    request.getType(),
                    request.getQuantity(),
                    stock.getSymbol(),
                    totalAmount,
                    agent.getName());
            subject.notifyObservers(message);
        }
    }
}
