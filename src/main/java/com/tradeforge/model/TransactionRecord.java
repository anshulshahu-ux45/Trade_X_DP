package com.tradeforge.model;

import java.sql.Timestamp;

/**
 * Model representing an immutable record of a completed trade in TradeForge.
 */
public class TransactionRecord {

    private int id;
    private String symbol;
    private String transactionType;
    private int quantity;
    private double price;
    private double charge;
    private double totalAmount;
    private Timestamp transactionDate;
    private int agentId;
    private String agentName;

    public TransactionRecord() {
    }

    public TransactionRecord(int id, String symbol, String transactionType, int quantity,
                             double price, double charge, double totalAmount, Timestamp transactionDate) {
        this(id, symbol, transactionType, quantity, price, charge, totalAmount, transactionDate, 0, "N/A");
    }

    public TransactionRecord(int id, String symbol, String transactionType, int quantity,
                             double price, double charge, double totalAmount, Timestamp transactionDate,
                             int agentId, String agentName) {
        this.id = id;
        this.symbol = symbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.charge = charge;
        this.totalAmount = totalAmount;
        this.transactionDate = transactionDate;
        this.agentId = agentId;
        this.agentName = agentName;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getCharge() {
        return charge;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public int getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName != null ? agentName : ("Agent #" + agentId);
    }
}
