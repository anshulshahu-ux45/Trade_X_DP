package com.tradeforge.factory;

/**
 * DESIGN PATTERN 2: FACTORY PATTERN (Creator)
 * 
 * Responsibility:
 * Encapsulates the instantiation logic for Transaction objects based on
 * the transaction type ("BUY" or "SELL").
 * 
 * The client code does not need to use 'new BuyTransaction()' or 'new SellTransaction()'.
 * It simply calls TransactionFactory.create(type).
 */
public final class TransactionFactory {

    private TransactionFactory() {
        // Prevent instantiation of utility factory
    }

    /**
     * Factory Method to create the appropriate Transaction subclass.
     * 
     * @param type "BUY" or "SELL" (case-insensitive)
     * @return concrete Transaction instance
     * @throws IllegalArgumentException if the type is unknown or invalid
     */
    public static Transaction create(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if ("BUY".equalsIgnoreCase(type.trim())) {
            return new BuyTransaction();
        }
        if ("SELL".equalsIgnoreCase(type.trim())) {
            return new SellTransaction();
        }
        throw new IllegalArgumentException("Unsupported transaction type: '" + type + "'. Supported types: BUY, SELL");
    }
}
