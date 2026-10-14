CREATE DATABASE IF NOT EXISTS tradeforge_db;
USE tradeforge_db;

DROP TABLE IF EXISTS transactions, portfolio, stocks, agents, users;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE stocks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    symbol VARCHAR(20) UNIQUE NOT NULL,
    company_name VARCHAR(120) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE agents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    commission_rate DECIMAL(5,2) NOT NULL DEFAULT 1.0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE portfolio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    stock_id INT NOT NULL,
    quantity INT NOT NULL,
    UNIQUE KEY user_stock(user_id, stock_id),
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(stock_id) REFERENCES stocks(id)
);

CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    stock_id INT NOT NULL,
    agent_id INT NOT NULL,
    transaction_type ENUM('BUY','SELL') NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    charge DECIMAL(12,2) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(stock_id) REFERENCES stocks(id),
    FOREIGN KEY(agent_id) REFERENCES agents(id)
);

-- Seed demo user
INSERT INTO users(id, username, password, account_type, balance, status) 
VALUES (1, 'anshul', '1234', 'NORMAL', 50000.00, 'ACTIVE');

-- Seed all 3 agents representing the Chain of Responsibility tiers
-- Tier 1 (1 - 500 stocks)    -> Agent 1
-- Tier 2 (501 - 700 stocks)  -> Agent 2
-- Tier 3 (> 700 stocks)      -> Agent 3
INSERT INTO agents(id, username, password, name, commission_rate, status) VALUES
(1, 'agent01', '1234', 'Agent 1 - Rahul Sharma (Tier 1: 1-500 stocks)', 1.0, 'ACTIVE'),
(2, 'agent02', '1234', 'Agent 2 - Priya Patel (Tier 2: 501-700 stocks)', 1.5, 'ACTIVE'),
(3, 'agent03', '1234', 'Agent 3 - Amit Verma (Tier 3: >700 stocks)', 2.0, 'ACTIVE');

-- Seed demo stock catalog
INSERT INTO stocks(id, symbol, company_name, price, quantity) VALUES
(1, 'TCS', 'Tata Consultancy Services', 3500.00, 1000),
(2, 'INFY', 'Infosys Limited', 1800.00, 1500),
(3, 'RELIANCE', 'Reliance Industries', 2900.00, 1200),
(4, 'HDFCBANK', 'HDFC Bank', 1600.00, 1300),
(5, 'ICICIBANK', 'ICICI Bank', 1250.00, 1400),
(6, 'WIPRO', 'Wipro Limited', 520.00, 1800),
(7, 'ITC', 'ITC Limited', 450.00, 2000),
(8, 'SBIN', 'State Bank of India', 800.00, 1700);
