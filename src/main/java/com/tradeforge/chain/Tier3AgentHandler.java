package com.tradeforge.chain;

import com.tradeforge.dao.AgentDAO;
import com.tradeforge.model.Agent;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Tier 3 Chain of Responsibility Handler:
 * Responsible for handling high-volume orders exceeding 700 stocks.
 * Assigns to Agent 3 (Amit Verma).
 */
public class Tier3AgentHandler extends AgentAssignmentHandler {

    @Override
    public Agent handle(int quantity, Connection connection) throws SQLException {
        if (quantity > 700) {
            Agent agent = null;
            if (connection != null) {
                agent = new AgentDAO().findById(3, connection);
            }
            if (agent == null) {
                // Fallback / offline representation
                agent = new Agent(3, "agent03", "1234", "Agent 3 - Amit Verma (Tier 3: >700 stocks)", 2.0, "ACTIVE");
            }
            return agent;
        }

        if (next != null) {
            return next.handle(quantity, connection);
        }

        return null;
    }
}
