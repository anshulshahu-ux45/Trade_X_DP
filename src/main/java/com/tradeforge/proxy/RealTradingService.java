package com.tradeforge.proxy;

import com.tradeforge.chain.AgentAssignmentHandler;
import com.tradeforge.chain.TradingRequest;
import com.tradeforge.model.Agent;
import com.tradeforge.observer.StockTradingSubject;
import com.tradeforge.singleton.DatabaseConnection;
import com.tradeforge.template.BuyTradingWorkflow;
import com.tradeforge.template.SellTradingWorkflow;
import com.tradeforge.template.TradeResult;
import com.tradeforge.template.TradingTemplate;
import java.sql.Connection;

/**
 * DESIGN PATTERN 3: PROXY PATTERN (Real Subject)
 * 
 * Performs the actual database-backed trade execution inside a transactional scope
 * using autoCommit(false), commit(), and rollback().
 * Coordinates with Chain of Responsibility (Agent Assignment) and Template Method.
 */
public class RealTradingService implements TradingService {

    private final StockTradingSubject subject;
    private final AgentAssignmentHandler agentChain;

    public RealTradingService(StockTradingSubject subject) {
        this.subject = subject;
        this.agentChain = AgentAssignmentHandler.createDefaultChain();
    }

    public RealTradingService(StockTradingSubject subject, AgentAssignmentHandler agentChain) {
        this.subject = subject;
        this.agentChain = agentChain;
    }

    @Override
    public TradeResult executeTrade(TradingRequest request, int authenticatedUserId) throws Exception {
        // Obtain database connection from Singleton
        Connection connection = DatabaseConnection.getInstance().getConnection();

        // 1. Determine assigned broker agent via Chain of Responsibility
        Agent assignedAgent = agentChain.handle(request.getQuantity(), connection);
        if (assignedAgent == null) {
            throw new IllegalStateException("No active broker agent could be assigned for quantity: " + request.getQuantity());
        }

        // 2. Select concrete workflow via Template Method Pattern
        TradingTemplate workflow;
        if ("BUY".equalsIgnoreCase(request.getType())) {
            workflow = new BuyTradingWorkflow(subject);
        } else if ("SELL".equalsIgnoreCase(request.getType())) {
            workflow = new SellTradingWorkflow(subject);
        } else {
            throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
        }

        // 3. Execute within an ACID JDBC transaction
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            TradeResult result = workflow.processTrade(request, assignedAgent, connection);

            connection.commit();
            return result;
        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (Exception rollbackException) {
                System.err.println("Rollback failed: " + rollbackException.getMessage());
            }
            throw exception;
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (Exception ignored) {
            }
        }
    }
}
