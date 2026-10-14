package com.tradeforge.chain;

import java.sql.Connection;

/**
 * DESIGN PATTERN 4: CHAIN OF RESPONSIBILITY (Validation Chain)
 * 
 * Abstract Handler for request validation.
 * Each handler checks a specific condition (e.g. user status, stock existence, balance, quantity)
 * and passes the request down the chain if valid.
 */
public abstract class ValidationHandler {

    protected ValidationHandler next;

    /**
     * Chains the next handler in the pipeline.
     */
    public ValidationHandler setNext(ValidationHandler handler) {
        this.next = handler;
        return handler;
    }

    /**
     * Executes the validation check and recursively delegates to next if successful.
     */
    public boolean validate(TradingRequest request, Connection connection) throws Exception {
        if (!check(request, connection)) {
            return false;
        }
        return next == null || next.validate(request, connection);
    }

    /**
     * Subclasses implement this specific check.
     */
    protected abstract boolean check(TradingRequest request, Connection connection) throws Exception;
}
