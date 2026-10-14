package com.tradeforge.factory;

/**
 * DESIGN PATTERN 2: FACTORY PATTERN (Interface / Product)
 * 
 * Defines the common behavior for all financial transaction types in TradeForge.
 */
public interface Transaction {

    /**
     * Returns the transaction type name (e.g., "BUY" or "SELL").
     */
    String getType();

    /**
     * Calculates the brokerage charge (default 2% of the trade value).
     */
    double calculateCharge(double price, int quantity);

    /**
     * Calculates the total amount involved in the transaction.
     * For BUY: base value + brokerage fee.
     * For SELL: base value - brokerage fee (net credited).
     */
    double calculateTotal(double price, int quantity);
}
