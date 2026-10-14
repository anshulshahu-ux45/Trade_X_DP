package com.tradeforge.model;

public class Stock {
    private int id; private String symbol; private String companyName; private double price; private int quantity;
    public Stock() { }
    public Stock(int id, String symbol, String companyName, double price, int quantity) { this.id=id; this.symbol=symbol; this.companyName=companyName; this.price=price; this.quantity=quantity; }
    public int getId() { return id; } public String getSymbol() { return symbol; } public String getCompanyName() { return companyName; } public double getPrice() { return price; } public int getQuantity() { return quantity; }
    public void setId(int value) { id=value; } public void setSymbol(String value) { symbol=value; } public void setCompanyName(String value) { companyName=value; } public void setPrice(double value) { price=value; } public void setQuantity(int value) { quantity=value; }
}
