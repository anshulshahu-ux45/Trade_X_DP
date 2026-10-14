package com.tradeforge.chain;

import com.tradeforge.dao.AgentDAO;
import com.tradeforge.model.Agent;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Tier 2 Chain of Responsibility Handler:
 * Responsible for handling orders of 501 to 700 stocks.
 * Assigns to Agent 2 (Priya Patel).
 */
public class Tier2AgentHandler extends AgentAssignmentHandler {

    @Override
    public Agent handle(int quantity, Connection connection) throws SQLException {
        if (quantity >= 501 && quantity <= 700) {
            Agent agent = null;
            if (connection != null) {
                agent = new AgentDAO().findById(2, connection);
            }
            if (agent == null) {
                // Fallback / offline representation
                agent = new Agent(2, "agent02", "1234", "Agent 2 - Priya Patel (Tier 2: 501-700 stocks)", 1.5, "ACTIVE");
            }
            return agent;
        }

        if (next != null) {
            return next.handle(quantity, connection);
        }

        return null;
    }
}
