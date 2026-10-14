package com.tradeforge.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String accountType;
    private double balance;
    private String status;

    public User() { }
    public User(int id, String username, String password, String accountType, double balance, String status) {
        this.id = id; this.username = username; this.password = password; this.accountType = accountType; this.balance = balance; this.status = status;
    }
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getUsername() { return username; } public void setUsername(String value) { username = value; }
    public String getPassword() { return password; } public void setPassword(String value) { password = value; }
    public String getAccountType() { return accountType; } public void setAccountType(String value) { accountType = value; }
    public double getBalance() { return balance; } public void setBalance(double value) { balance = value; }
    public String getStatus() { return status; } public void setStatus(String value) { status = value; }
}
