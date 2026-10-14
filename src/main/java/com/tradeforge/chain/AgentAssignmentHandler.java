package com.tradeforge.chain;

import com.tradeforge.model.Agent;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DESIGN PATTERN 4: CHAIN OF RESPONSIBILITY (Agent Assignment Chain)
 * 
 * Abstract Handler for assigning an order to a broker agent based on quantity:
 * - 1 to 500 stocks: Agent 1
 * - 501 to 700 stocks: Agent 2
 * - More than 700 stocks: Agent 3
 */
public abstract class AgentAssignmentHandler {

    protected AgentAssignmentHandler next;

    /**
     * Chains the next tier handler.
     */
    public AgentAssignmentHandler setNext(AgentAssignmentHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Resolves the assigned Agent for the given quantity.
     * 
     * @param quantity number of shares in the trade
     * @param connection database connection to load agent record (can be null for offline preview)
     * @return Assigned Agent instance
     */
    public abstract Agent handle(int quantity, Connection connection) throws SQLException;

    /**
     * Helper to create the default pre-configured 3-tier chain.
     */
    public static AgentAssignmentHandler createDefaultChain() {
        AgentAssignmentHandler tier1 = new Tier1AgentHandler();
        AgentAssignmentHandler tier2 = new Tier2AgentHandler();
        AgentAssignmentHandler tier3 = new Tier3AgentHandler();

        tier1.setNext(tier2).setNext(tier3);
        return tier1;
    }
}
