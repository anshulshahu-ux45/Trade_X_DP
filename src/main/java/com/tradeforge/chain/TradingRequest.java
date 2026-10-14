package com.tradeforge.chain;

import com.tradeforge.memento.TradeMemento;
import com.tradeforge.prototype.TradePrototype;

/**
 * Encapsulates a trading request submitted by a user.
 * 
 * Demonstrates:
 * - PROTOTYPE PATTERN: Implements TradePrototype to clone independent trade requests.
 * - MEMENTO PATTERN (Originator): Can create a TradeMemento and restore from one.
 */
public class TradingRequest implements TradePrototype {

    private int userId;
    private int stockId;
    private int quantity;
    private String type; // "BUY" or "SELL"
    private String error;

    public TradingRequest(int userId, int stockId, int quantity, String type) {
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.type = type != null ? type.toUpperCase().trim() : "";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type != null ? type.toUpperCase().trim() : "";
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * DESIGN PATTERN 10: PROTOTYPE PATTERN
     * Clones this trading request into an independent new object.
     */
    @Override
    public TradingRequest cloneTrade() {
        TradingRequest cloned = new TradingRequest(this.userId, this.stockId, this.quantity, this.type);
        cloned.setError(this.error);
        return cloned;
    }

    /**
     * DESIGN PATTERN 9: MEMENTO PATTERN (Originator step: Create Memento)
     */
    public TradeMemento createMemento() {
        return new TradeMemento(this.stockId, this.type, this.quantity);
    }

    /**
     * DESIGN PATTERN 9: MEMENTO PATTERN (Originator step: Restore from Memento)
     */
    public void restoreFromMemento(TradeMemento memento) {
        if (memento != null) {
            this.stockId = memento.getStockId();
            this.type = memento.getType();
            this.quantity = memento.getQuantity();
        }
    }

    @Override
    public String toString() {
        return "TradingRequest [userId=" + userId + ", stockId=" + stockId + ", quantity=" + quantity + ", type=" + type + "]";
    }
}
