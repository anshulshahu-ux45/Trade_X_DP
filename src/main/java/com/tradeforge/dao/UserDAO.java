package com.tradeforge.dao;

import com.tradeforge.model.User;
import com.tradeforge.singleton.DatabaseConnection;
import java.sql.*;

public class UserDAO {
    public User login(String username, String password) throws SQLException { try (Connection connection = DatabaseConnection.getInstance().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) { statement.setString(1, username); statement.setString(2, password); try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; } } }
    public User findById(int id, Connection connection) throws SQLException { try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id=?")) { statement.setInt(1, id); try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; } } }
    public User findById(int id) throws SQLException { try (Connection connection = DatabaseConnection.getInstance().getConnection()) { return findById(id, connection); } }
    public void updateBalance(int id, double balance, Connection connection) throws SQLException { try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance=? WHERE id=?")) { statement.setDouble(1, balance); statement.setInt(2, id); statement.executeUpdate(); } }
    private User map(ResultSet result) throws SQLException { return new User(result.getInt("id"), result.getString("username"), result.getString("password"), result.getString("account_type"), result.getDouble("balance"), result.getString("status")); }
}
