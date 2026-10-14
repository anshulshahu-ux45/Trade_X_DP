package com.tradeforge.mediator;

import com.tradeforge.chain.AgentAssignmentHandler;
import com.tradeforge.chain.TradingRequest;
import com.tradeforge.dao.AgentDAO;
import com.tradeforge.dao.PortfolioDAO;
import com.tradeforge.dao.StockDAO;
import com.tradeforge.dao.TransactionDAO;
import com.tradeforge.dao.UserDAO;
import com.tradeforge.memento.TradeCaretaker;
import com.tradeforge.model.Agent;
import com.tradeforge.model.Portfolio;
import com.tradeforge.model.Stock;
import com.tradeforge.model.TransactionRecord;
import com.tradeforge.model.User;
import com.tradeforge.observer.StockTradingSubject;
import com.tradeforge.observer.UserNotificationObserver;
import com.tradeforge.proxy.RealTradingService;
import com.tradeforge.proxy.TradingService;
import com.tradeforge.proxy.TradingServiceProxy;
import com.tradeforge.template.TradeResult;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * DESIGN PATTERN 7: MEDIATOR PATTERN
 * 
 * Responsibility:
 * Central mediator that decouples the Controller / GUI from the underlying complexity
 * of DAOs, Proxy, Chain of Responsibility, Template Method, and Observer.
 * 
 * Instead of the Controller directly interacting with 8 different components,
 * it makes clean, high-level requests to the TradingMediator.
 */
public class TradingMediator {

    // DAOs
    private final UserDAO userDAO;
    private final StockDAO stockDAO;
    private final PortfolioDAO portfolioDAO;
    private final TransactionDAO transactionDAO;
    private final AgentDAO agentDAO;

    // Observer components
    private final StockTradingSubject tradingSubject;
    private final UserNotificationObserver userNotificationObserver;

    // Chain of Responsibility for broker tier assignment
    private final AgentAssignmentHandler agentChain;

    // Proxy wrapping the RealTradingService
    private final TradingService tradingService;

    // Memento Caretaker for saving/restoring UI trade drafts
    private final TradeCaretaker tradeCaretaker;

    public TradingMediator() {
        this.userDAO = new UserDAO();
        this.stockDAO = new StockDAO();
        this.portfolioDAO = new PortfolioDAO();
        this.transactionDAO = new TransactionDAO();
        this.agentDAO = new AgentDAO();

        this.tradingSubject = new StockTradingSubject();
        this.userNotificationObserver = new UserNotificationObserver();
        this.tradingSubject.attach(userNotificationObserver);

        this.agentChain = AgentAssignmentHandler.createDefaultChain();
        this.tradingService = new TradingServiceProxy(new RealTradingService(tradingSubject, agentChain));
        this.tradeCaretaker = new TradeCaretaker();
    }

    /**
     * Authenticates a user against the database.
     */
    public User login(String username, String password) throws SQLException {
        return userDAO.login(username, password);
    }

    /**
     * Executes a trade by delegating through the TradingServiceProxy.
     */
    public TradeResult executeTrade(TradingRequest request, int authenticatedUserId) throws Exception {
        return tradingService.executeTrade(request, authenticatedUserId);
    }

    /**
     * Determines the assigned broker agent using the Chain of Responsibility.
     */
    public Agent determineAgent(int quantity) {
        try {
            return agentChain.handle(quantity, null);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Loads available stock catalog.
     */
    public List<Stock> getAllStocks() throws SQLException {
        return stockDAO.findAll();
    }

    /**
     * Loads user portfolio holdings.
     */
    public List<Portfolio> getUserPortfolio(int userId) throws SQLException {
        return portfolioDAO.findByUser(userId);
    }

    /**
     * Loads user transaction audit history.
     */
    public List<TransactionRecord> getUserTransactions(int userId) throws SQLException {
        return transactionDAO.findByUser(userId);
    }

    /**
     * Refreshes user details (e.g. balance).
     */
    public User getUser(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    /**
     * Registers a UI listener to receive Observer notifications.
     */
    public void registerNotificationListener(Consumer<String> listener) {
        userNotificationObserver.setUiListener(listener);
    }

    /**
     * Accesses the Memento caretaker for saving and undoing trade form drafts.
     */
    public TradeCaretaker getTradeCaretaker() {
        return tradeCaretaker;
    }

    public StockTradingSubject getTradingSubject() {
        return tradingSubject;
    }
}
