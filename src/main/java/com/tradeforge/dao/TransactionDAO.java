package com.tradeforge.dao;

import com.tradeforge.model.TransactionRecord;
import com.tradeforge.singleton.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for recorded stock transactions.
 */
public class TransactionDAO {

    public void insert(int userId, int stockId, int agentId, String type, int quantity,
                       double price, double charge, double total, Connection connection) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, stock_id, agent_id, transaction_type, quantity, price, charge, total_amount, transaction_date) "
                   + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, stockId);
            statement.setInt(3, agentId);
            statement.setString(4, type);
            statement.setInt(5, quantity);
            statement.setDouble(6, price);
            statement.setDouble(7, charge);
            statement.setDouble(8, total);
            statement.executeUpdate();
        }
    }

    public List<TransactionRecord> findByUser(int userId) throws SQLException {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = "SELECT t.*, s.symbol, a.name AS agent_name "
                   + "FROM transactions t "
                   + "JOIN stocks s ON s.id = t.stock_id "
                   + "LEFT JOIN agents a ON a.id = t.agent_id "
                   + "WHERE t.user_id = ? "
                   + "ORDER BY t.transaction_date DESC";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    list.add(new TransactionRecord(
                            result.getInt("id"),
                            result.getString("symbol"),
                            result.getString("transaction_type"),
                            result.getInt("quantity"),
                            result.getDouble("price"),
                            result.getDouble("charge"),
                            result.getDouble("total_amount"),
                            result.getTimestamp("transaction_date"),
                            result.getInt("agent_id"),
                            result.getString("agent_name")
                    ));
                }
            }
        }
        return list;
    }
}
