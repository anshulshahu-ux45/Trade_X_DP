package com.tradeforge.factory;

/**
 * Concrete Product for SELL transactions in the Factory Pattern.
 * 
 * For a SELL order:
 * Net payout = Base stock value - Brokerage charges (2%).
 */
public class SellTransaction implements Transaction {

    private static final double BROKERAGE_RATE = 0.02; // 2% charge

    @Override
    public String getType() {
        return "SELL";
    }

    @Override
    public double calculateCharge(double price, int quantity) {
        return (price * quantity) * BROKERAGE_RATE;
    }

    @Override
    public double calculateTotal(double price, int quantity) {
        double base = price * quantity;
        return base - calculateCharge(price, quantity);
    }
}
