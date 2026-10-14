package com.tradeforge.chain;

import com.tradeforge.dao.AgentDAO;
import com.tradeforge.model.Agent;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Tier 1 Chain of Responsibility Handler:
 * Responsible for handling orders of 1 to 500 stocks.
 * Assigns to Agent 1 (Rahul Sharma).
 */
public class Tier1AgentHandler extends AgentAssignmentHandler {

    @Override
    public Agent handle(int quantity, Connection connection) throws SQLException {
        if (quantity >= 1 && quantity <= 500) {
            Agent agent = null;
            if (connection != null) {
                agent = new AgentDAO().findById(1, connection);
            }
            if (agent == null) {
                // Fallback / offline representation
                agent = new Agent(1, "agent01", "1234", "Agent 1 - Rahul Sharma (Tier 1: 1-500 stocks)", 1.0, "ACTIVE");
            }
            return agent;
        }

        if (next != null) {
            return next.handle(quantity, connection);
        }

        return null;
    }
}
