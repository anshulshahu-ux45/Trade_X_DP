package com.tradeforge.builder;

import com.tradeforge.chain.TradingRequest;

/**
 * DESIGN PATTERN 6: BUILDER PATTERN
 * 
 * Responsibility:
 * Constructs a TradingRequest object step by step using fluent setter chaining.
 * Validates that all required parameters are valid and present upon build().
 * 
 * Example usage:
 * TradingRequest request = new TradeBuilder()
 *     .setUserId(userId)
 *     .setStockId(stockId)
 *     .setType("BUY")
 *     .setQuantity(50)
 *     .build();
 */
public class TradeBuilder {

    private int userId;
    private int stockId;
    private String type;
    private int quantity;

    public TradeBuilder() {
    }

    public TradeBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public TradeBuilder setStockId(int stockId) {
        this.stockId = stockId;
        return this;
    }

    public TradeBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public TradeBuilder setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * Initializes the builder from an existing prototype request.
     */
    public TradeBuilder fromPrototype(TradingRequest prototype) {
        if (prototype != null) {
            this.userId = prototype.getUserId();
            this.stockId = prototype.getStockId();
            this.type = prototype.getType();
            this.quantity = prototype.getQuantity();
        }
        return this;
    }

    /**
     * Validates required fields and constructs the TradingRequest object.
     * 
     * @return a valid TradingRequest instance
     * @throws IllegalArgumentException if any required field is invalid
     */
    public TradingRequest build() {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid User ID: User must be authenticated.");
        }
        if (stockId <= 0) {
            throw new IllegalArgumentException("Please select a valid stock.");
        }
        if (type == null || (!"BUY".equalsIgnoreCase(type.trim()) && !"SELL".equalsIgnoreCase(type.trim()))) {
            throw new IllegalArgumentException("Transaction type must be BUY or SELL.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        return new TradingRequest(userId, stockId, quantity, type.trim().toUpperCase());
    }
}
