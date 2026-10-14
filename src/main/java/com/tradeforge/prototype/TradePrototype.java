package com.tradeforge.prototype;

import com.tradeforge.chain.TradingRequest;

/**
 * DESIGN PATTERN 10: PROTOTYPE PATTERN
 * 
 * Defines the cloning interface to duplicate an existing trade request.
 * Used in the GUI when a user clicks "Duplicate Trade" to create an independent,
 * editable copy without triggering immediate database execution.
 */
public interface TradePrototype {

    /**
     * Creates and returns a deep, independent clone of the trade request.
     */
    TradingRequest cloneTrade();
}
