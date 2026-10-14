package com.tradeforge.memento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DESIGN PATTERN 9: MEMENTO PATTERN (Memento)
 * 
 * Holds an immutable snapshot of an editable Trade Request state.
 * Stores stock ID, transaction type, quantity, and a timestamp of when it was saved.
 * 
 * Note: This restores the UI draft state in the form. It does NOT reverse committed
 * database transactions in MySQL.
 */
public final class TradeMemento {

    private final int stockId;
    private final String type;
    private final int quantity;
    private final String timestamp;

    public TradeMemento(int stockId, String type, int quantity) {
        this.stockId = stockId;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public int getStockId() {
        return stockId;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TradeMemento [stockId=" + stockId + ", type=" + type + ", quantity=" + quantity + ", savedAt=" + timestamp + "]";
    }
}
