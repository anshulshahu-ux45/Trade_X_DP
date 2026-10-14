package com.tradeforge.model;

public class Agent {
    private int id; private String username; private String password; private String name; private double commissionRate; private String status;
    public Agent() { }
    public Agent(int id, String username, String password, String name, double commissionRate, String status) { this.id=id; this.username=username; this.password=password; this.name=name; this.commissionRate=commissionRate; this.status=status; }
    public int getId() { return id; } public String getUsername() { return username; } public String getPassword() { return password; } public String getName() { return name; } public double getCommissionRate() { return commissionRate; } public String getStatus() { return status; }
}
