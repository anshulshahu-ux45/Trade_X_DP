package com.tradeforge.dao;

import com.tradeforge.model.Stock;
import com.tradeforge.singleton.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class StockDAO {
    public List<Stock> findAll() throws SQLException { List<Stock> stocks = new ArrayList<>(); try (Connection connection=DatabaseConnection.getInstance().getConnection(); PreparedStatement statement=connection.prepareStatement("SELECT * FROM stocks ORDER BY symbol"); ResultSet result=statement.executeQuery()) { while(result.next()) stocks.add(map(result)); } return stocks; }
    public Stock findById(int id, Connection connection) throws SQLException { try(PreparedStatement statement=connection.prepareStatement("SELECT * FROM stocks WHERE id=?")){ statement.setInt(1,id); try(ResultSet result=statement.executeQuery()){ return result.next()?map(result):null; } } }
    public Stock findById(int id) throws SQLException { try(Connection connection=DatabaseConnection.getInstance().getConnection()){ return findById(id,connection); } }
    public void updateQuantity(int id, int quantity, Connection connection) throws SQLException { try(PreparedStatement statement=connection.prepareStatement("UPDATE stocks SET quantity=? WHERE id=?")){ statement.setInt(1,quantity); statement.setInt(2,id); statement.executeUpdate(); } }
    private Stock map(ResultSet result) throws SQLException { return new Stock(result.getInt("id"),result.getString("symbol"),result.getString("company_name"),result.getDouble("price"),result.getInt("quantity")); }
}
