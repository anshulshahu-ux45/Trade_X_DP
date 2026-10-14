package com.tradeforge.chain;

import com.tradeforge.dao.PortfolioDAO;
import com.tradeforge.dao.StockDAO;
import com.tradeforge.model.Portfolio;
import com.tradeforge.model.Stock;
import java.sql.Connection;

/**
 * Concrete Validation Handler: Verifies order quantity validity:
 * 1. Quantity must be > 0.
 * 2. For BUY orders, market inventory must have sufficient shares.
 * 3. For SELL orders, user's portfolio holding must have enough shares.
 */
public class QuantityValidationHandler extends ValidationHandler {

    @Override
    protected boolean check(TradingRequest request, Connection connection) throws Exception {
        if (request.getQuantity() <= 0) {
            request.setError("Order quantity must be greater than zero.");
            return false;
        }

        Stock stock = new StockDAO().findById(request.getStockId(), connection);
        if (stock == null) {
            request.setError("Target stock not found.");
            return false;
        }

        if ("BUY".equalsIgnoreCase(request.getType())) {
            if (stock.getQuantity() < request.getQuantity()) {
                request.setError(String.format("Insufficient market stock. Requested: %d, Available: %d shares.",
                        request.getQuantity(), stock.getQuantity()));
                return false;
            }
        } else if ("SELL".equalsIgnoreCase(request.getType())) {
            Portfolio holding = new PortfolioDAO().findHolding(request.getUserId(), request.getStockId(), connection);
            if (holding == null || holding.getQuantity() < request.getQuantity()) {
                int owned = holding != null ? holding.getQuantity() : 0;
                request.setError(String.format("You do not own enough shares to sell. Requested: %d, Owned: %d shares.",
                        request.getQuantity(), owned));
                return false;
            }
        }
        return true;
    }
}
