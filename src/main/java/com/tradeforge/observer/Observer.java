package com.tradeforge.observer;

/**
 * DESIGN PATTERN 5: OBSERVER PATTERN (Observer Interface)
 * 
 * Defines the update contract for observers that listen to trade execution events.
 */
public interface Observer {

    /**
     * Called by the Subject when a trade event occurs.
     * 
     * @param message notification message describing the trade
     */
    void update(String message);
}
