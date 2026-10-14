package com.tradeforge.dao;

import com.tradeforge.model.Agent;
import com.tradeforge.singleton.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for broker agents.
 */
public class AgentDAO {

    public Agent findById(int id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM agents WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? map(result) : null;
            }
        }
    }

    public Agent findById(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            return findById(id, connection);
        }
    }

    public Agent getAvailableAgent() throws SQLException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM agents WHERE status='ACTIVE' LIMIT 1");
             ResultSet result = statement.executeQuery()) {
            return result.next() ? map(result) : null;
        }
    }

    public List<Agent> findAll() throws SQLException {
        List<Agent> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM agents ORDER BY id ASC");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                list.add(map(result));
            }
        }
        return list;
    }

    private Agent map(ResultSet result) throws SQLException {
        return new Agent(
                result.getInt("id"),
                result.getString("username"),
                result.getString("password"),
                result.getString("name"),
                result.getDouble("commission_rate"),
                result.getString("status")
        );
    }
}
