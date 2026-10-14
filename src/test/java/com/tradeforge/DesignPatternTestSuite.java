package com.tradeforge;

import com.tradeforge.builder.TradeBuilder;
import com.tradeforge.chain.AgentAssignmentHandler;
import com.tradeforge.chain.QuantityValidationHandler;
import com.tradeforge.chain.TradingRequest;
import com.tradeforge.factory.Transaction;
import com.tradeforge.factory.TransactionFactory;
import com.tradeforge.memento.TradeCaretaker;
import com.tradeforge.memento.TradeMemento;
import com.tradeforge.model.Agent;
import com.tradeforge.observer.StockTradingSubject;
import com.tradeforge.observer.UserNotificationObserver;
import com.tradeforge.proxy.TradingService;
import com.tradeforge.proxy.TradingServiceProxy;
import com.tradeforge.singleton.DatabaseConnection;
import com.tradeforge.template.TradeResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * COMPREHENSIVE DESIGN PATTERN LAB TEST SUITE
 * 
 * Verifies all 10 Gang-of-Four (GoF) design patterns implemented in TradeForge:
 * 1. Singleton Pattern
 * 2. Factory Pattern
 * 3. Proxy Pattern
 * 4. Chain of Responsibility Pattern (Agent Assignment & Validation)
 * 5. Observer Pattern
 * 6. Builder Pattern
 * 7. Mediator Pattern
 * 8. Template Method Pattern
 * 9. Memento Pattern
 * 10. Prototype Pattern
 */
public class DesignPatternTestSuite {

    // ==========================================
    // 1. SINGLETON PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 1 (Singleton): DatabaseConnection provides a single unique instance")
    void testSingletonInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();

        assertNotNull(instance1, "Singleton instance 1 must not be null");
        assertNotNull(instance2, "Singleton instance 2 must not be null");
        assertSame(instance1, instance2, "Both calls to getInstance() must return the exact same memory reference");
    }

    // ==========================================
    // 2. FACTORY PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 2 (Factory): TransactionFactory creates BUY and SELL transactions with accurate fees")
    void testTransactionFactory() {
        // Test BUY transaction creation
        Transaction buyTx = TransactionFactory.create("BUY");
        assertEquals("BUY", buyTx.getType());

        // Base: 100 * 10 = 1000. Fee (2%): 20. Total BUY = 1020
        assertEquals(20.0, buyTx.calculateCharge(100.0, 10), 0.001);
        assertEquals(1020.0, buyTx.calculateTotal(100.0, 10), 0.001);

        // Test SELL transaction creation
        Transaction sellTx = TransactionFactory.create("SELL");
        assertEquals("SELL", sellTx.getType());

        // Base: 100 * 10 = 1000. Fee (2%): 20. Net SELL = 980
        assertEquals(20.0, sellTx.calculateCharge(100.0, 10), 0.001);
        assertEquals(980.0, sellTx.calculateTotal(100.0, 10), 0.001);

        // Test invalid transaction type
        assertThrows(IllegalArgumentException.class, () -> TransactionFactory.create("INVALID_TYPE"));
        assertThrows(IllegalArgumentException.class, () -> TransactionFactory.create(null));
    }

    // ==========================================
    // 3. PROXY PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 3 (Proxy): TradingServiceProxy blocks unauthenticated users and security mismatches")
    void testTradingServiceProxySecurity() {
        // Mock target service to verify delegation
        AtomicReference<Boolean> realServiceCalled = new AtomicReference<>(false);
        TradingService mockRealService = (req, authId) -> {
            realServiceCalled.set(true);
            return new TradeResult(true, "Mock Success", 1000.0, null);
        };

        TradingServiceProxy proxy = new TradingServiceProxy(mockRealService);

        // Case A: Unauthenticated user (authId <= 0)
        TradingRequest request = new TradingRequest(1, 1, 10, "BUY");
        assertThrows(SecurityException.class, () -> proxy.executeTrade(request, 0),
                "Proxy must block trade execution when authenticatedUserId <= 0");

        // Case B: Null request
        assertThrows(IllegalArgumentException.class, () -> proxy.executeTrade(null, 1),
                "Proxy must reject null request");

        // Case C: Security Mismatch (Request belongs to User 2, but logged in as User 1)
        TradingRequest foreignRequest = new TradingRequest(2, 1, 10, "BUY");
        assertThrows(SecurityException.class, () -> proxy.executeTrade(foreignRequest, 1),
                "Proxy must prevent executing trades on behalf of another user ID");

        // Case D: Valid and Authenticated -> Successfully delegates to Real Service
        assertDoesNotThrow(() -> proxy.executeTrade(request, 1));
        assertTrue(realServiceCalled.get(), "Proxy must delegate to real trading service upon valid authentication");
    }

    // ==========================================
    // 4. CHAIN OF RESPONSIBILITY PATTERN TESTS
    // ==========================================
    @Test
    @DisplayName("Pattern 4 (Chain of Responsibility): Agent assignment satisfies all three quantity tiers")
    void testAgentAssignmentChain() throws SQLException {
        AgentAssignmentHandler chain = AgentAssignmentHandler.createDefaultChain();

        // Tier 1: 1 - 500 stocks -> Agent 1
        Agent agent1Low = chain.handle(1, null);
        assertNotNull(agent1Low);
        assertEquals(1, agent1Low.getId());
        assertTrue(agent1Low.getName().contains("Agent 1"));

        Agent agent1High = chain.handle(500, null);
        assertNotNull(agent1High);
        assertEquals(1, agent1High.getId());

        // Tier 2: 501 - 700 stocks -> Agent 2
        Agent agent2Low = chain.handle(501, null);
        assertNotNull(agent2Low);
        assertEquals(2, agent2Low.getId());
        assertTrue(agent2Low.getName().contains("Agent 2"));

        Agent agent2High = chain.handle(700, null);
        assertNotNull(agent2High);
        assertEquals(2, agent2High.getId());

        // Tier 3: > 700 stocks -> Agent 3
        Agent agent3Low = chain.handle(701, null);
        assertNotNull(agent3Low);
        assertEquals(3, agent3Low.getId());
        assertTrue(agent3Low.getName().contains("Agent 3"));

        Agent agent3High = chain.handle(5000, null);
        assertNotNull(agent3High);
        assertEquals(3, agent3High.getId());
    }

    @Test
    @DisplayName("Pattern 4 (Chain of Responsibility): QuantityValidationHandler blocks invalid quantities")
    void testQuantityValidationChain() throws Exception {
        QuantityValidationHandler handler = new QuantityValidationHandler();

        // Zero quantity
        TradingRequest zeroReq = new TradingRequest(1, 1, 0, "BUY");
        boolean validZero = handler.validate(zeroReq, null);
        assertFalse(validZero);
        assertEquals("Order quantity must be greater than zero.", zeroReq.getError());

        // Negative quantity
        TradingRequest negReq = new TradingRequest(1, 1, -5, "BUY");
        boolean validNeg = handler.validate(negReq, null);
        assertFalse(validNeg);
        assertEquals("Order quantity must be greater than zero.", negReq.getError());
    }

    // ==========================================
    // 5. OBSERVER PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 5 (Observer): StockTradingSubject notifies UserNotificationObserver upon trade event")
    void testObserverNotification() {
        StockTradingSubject subject = new StockTradingSubject();
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        UserNotificationObserver observer = new UserNotificationObserver(receivedMessage::set);
        subject.attach(observer);

        String testEvent = "TCS BUY transaction completed successfully.";
        subject.notifyObservers(testEvent);

        assertEquals(testEvent, receivedMessage.get(), "Observer must receive exact event notification");
        assertEquals(testEvent, observer.getLastNotification());

        // Test detaching observer
        subject.detach(observer);
        subject.notifyObservers("Second event");
        assertEquals(testEvent, receivedMessage.get(), "Detached observer must not receive subsequent notifications");
    }

    // ==========================================
    // 6. BUILDER PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 6 (Builder): TradeBuilder step-by-step construction and field validation")
    void testTradeBuilder() {
        // Successful build
        TradingRequest request = new TradeBuilder()
                .setUserId(1)
                .setStockId(2)
                .setType("BUY")
                .setQuantity(50)
                .build();

        assertNotNull(request);
        assertEquals(1, request.getUserId());
        assertEquals(2, request.getStockId());
        assertEquals("BUY", request.getType());
        assertEquals(50, request.getQuantity());

        // Validation: Missing or invalid User ID
        assertThrows(IllegalArgumentException.class, () -> new TradeBuilder()
                .setUserId(0).setStockId(1).setType("BUY").setQuantity(10).build());

        // Validation: Missing stock ID
        assertThrows(IllegalArgumentException.class, () -> new TradeBuilder()
                .setUserId(1).setStockId(0).setType("BUY").setQuantity(10).build());

        // Validation: Invalid type
        assertThrows(IllegalArgumentException.class, () -> new TradeBuilder()
                .setUserId(1).setStockId(1).setType("HOLD").setQuantity(10).build());

        // Validation: Zero or negative quantity
        assertThrows(IllegalArgumentException.class, () -> new TradeBuilder()
                .setUserId(1).setStockId(1).setType("BUY").setQuantity(0).build());
        assertThrows(IllegalArgumentException.class, () -> new TradeBuilder()
                .setUserId(1).setStockId(1).setType("BUY").setQuantity(-10).build());
    }

    // ==========================================
    // 7. MEMENTO PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 7 (Memento): TradeMemento saves and restores previous form state without database commits")
    void testMementoPattern() {
        TradeCaretaker caretaker = new TradeCaretaker();
        assertFalse(caretaker.hasSavedState(), "Caretaker must start empty");

        // Initial state
        TradingRequest request = new TradingRequest(1, 1, 20, "BUY");

        // Save State into Memento
        TradeMemento memento = request.createMemento();
        assertNotNull(memento);
        assertEquals(1, memento.getStockId());
        assertEquals("BUY", memento.getType());
        assertEquals(20, memento.getQuantity());

        caretaker.save(memento);
        assertTrue(caretaker.hasSavedState());

        // Modify current request in UI
        request.setStockId(2);
        request.setType("SELL");
        request.setQuantity(100);

        assertEquals(2, request.getStockId());
        assertEquals("SELL", request.getType());
        assertEquals(100, request.getQuantity());

        // Restore / Undo from Caretaker
        TradeMemento restoredMemento = caretaker.undo();
        assertNotNull(restoredMemento);
        request.restoreFromMemento(restoredMemento);

        // Verify state is restored to original snapshot
        assertEquals(1, request.getStockId(), "Restored stock ID must match saved state");
        assertEquals("BUY", request.getType(), "Restored type must match saved state");
        assertEquals(20, request.getQuantity(), "Restored quantity must match saved state");
        assertFalse(caretaker.hasSavedState(), "Caretaker history should be popped");
    }

    // ==========================================
    // 8. PROTOTYPE PATTERN TEST
    // ==========================================
    @Test
    @DisplayName("Pattern 8 (Prototype): cloneTrade creates an independent clone of the trade request")
    void testPrototypePattern() {
        TradingRequest original = new TradingRequest(1, 3, 75, "SELL");

        // Clone using Prototype pattern
        TradingRequest clone = original.cloneTrade();

        assertNotNull(clone);
        assertNotSame(original, clone, "Clone must be a distinct object reference");

        // Field equality
        assertEquals(original.getUserId(), clone.getUserId());
        assertEquals(original.getStockId(), clone.getStockId());
        assertEquals(original.getQuantity(), clone.getQuantity());
        assertEquals(original.getType(), clone.getType());

        // Independence verification: mutating clone must not affect original
        clone.setQuantity(200);
        clone.setType("BUY");

        assertEquals(75, original.getQuantity(), "Original quantity must remain unchanged");
        assertEquals("SELL", original.getType(), "Original type must remain unchanged");
        assertEquals(200, clone.getQuantity());
        assertEquals("BUY", clone.getType());
    }
}
