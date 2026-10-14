package com.tradeforge.observer;

import java.util.function.Consumer;

/**
 * DESIGN PATTERN 5: OBSERVER PATTERN (Concrete Observer)
 * 
 * Receives notifications about successful BUY and SELL transactions
 * and updates the Swing user interface via registered listeners.
 */
public class UserNotificationObserver implements TradingObserver {

    private String lastNotification;
    private Consumer<String> uiListener;

    public UserNotificationObserver() {
    }

    public UserNotificationObserver(Consumer<String> uiListener) {
        this.uiListener = uiListener;
    }

    public void setUiListener(Consumer<String> uiListener) {
        this.uiListener = uiListener;
    }

    @Override
    public void update(String message) {
        this.lastNotification = message;
        if (uiListener != null) {
            uiListener.accept(message);
        }
    }

    public String getLastNotification() {
        return lastNotification;
    }
}
