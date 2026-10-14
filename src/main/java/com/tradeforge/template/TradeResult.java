package com.tradeforge.template;

import com.tradeforge.model.Agent;

/**
 * Result object returned after executing a trade workflow.
 */
public class TradeResult {

    private final boolean success;
    private final String message;
    private final double totalAmount;
    private final Agent assignedAgent;

    public TradeResult(boolean success, String message, double totalAmount, Agent assignedAgent) {
        this.success = success;
        this.message = message;
        this.totalAmount = totalAmount;
        this.assignedAgent = assignedAgent;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Agent getAssignedAgent() {
        return assignedAgent;
    }

    @Override
    public String toString() {
        return "TradeResult [success=" + success + ", message=" + message + ", totalAmount=" + totalAmount
                + ", assignedAgent=" + (assignedAgent != null ? assignedAgent.getName() : "None") + "]";
    }
}
