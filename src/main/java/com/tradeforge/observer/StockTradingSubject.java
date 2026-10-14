package com.tradeforge.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN 5: OBSERVER PATTERN (Subject)
 * 
 * Maintains a registry of TradingObserver objects and broadcasts
 * notifications upon successful completion of stock trades.
 */
public class StockTradingSubject {

    private final List<TradingObserver> observers = new ArrayList<>();

    /**
     * Attaches an observer to the subject.
     */
    public void attach(TradingObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Detaches an observer from the subject.
     */
    public void detach(TradingObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers with an event message.
     */
    public void notifyObservers(String message) {
        for (TradingObserver observer : observers) {
            observer.update(message);
        }
    }

    public List<TradingObserver> getObservers() {
        return new ArrayList<>(observers);
    }
}
