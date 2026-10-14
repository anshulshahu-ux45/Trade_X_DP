package com.tradeforge.factory;

/**
 * Concrete Product for BUY transactions in the Factory Pattern.
 * 
 * For a BUY order:
 * Total to pay = Base stock value + Brokerage charges (2%).
 */
public class BuyTransaction implements Transaction {

    private static final double BROKERAGE_RATE = 0.02; // 2% charge

    @Override
    public String getType() {
        return "BUY";
    }

    @Override
    public double calculateCharge(double price, int quantity) {
        return (price * quantity) * BROKERAGE_RATE;
    }

    @Override
    public double calculateTotal(double price, int quantity) {
        double base = price * quantity;
        return base + calculateCharge(price, quantity);
    }
}
