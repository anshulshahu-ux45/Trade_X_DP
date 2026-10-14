package com.tradeforge.model;

public class Portfolio {
    private int id; private int userId; private int stockId; private int quantity; private String symbol; private String companyName; private double currentPrice;
    public Portfolio() { }
    public Portfolio(int id, int userId, int stockId, int quantity, String symbol, String companyName, double currentPrice) { this.id=id; this.userId=userId; this.stockId=stockId; this.quantity=quantity; this.symbol=symbol; this.companyName=companyName; this.currentPrice=currentPrice; }
    public int getId() { return id; } public int getUserId() { return userId; } public int getStockId() { return stockId; } public int getQuantity() { return quantity; } public String getSymbol() { return symbol; } public String getCompanyName() { return companyName; } public double getCurrentPrice() { return currentPrice; }
    public void setQuantity(int value) { quantity=value; }
}
