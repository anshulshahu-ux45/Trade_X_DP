package com.tradeforge.chain;

import com.tradeforge.dao.StockDAO;
import com.tradeforge.model.Stock;
import java.sql.Connection;

/**
 * Concrete Validation Handler: Verifies the target stock exists in the market.
 */
public class StockValidationHandler extends ValidationHandler {

    @Override
    protected boolean check(TradingRequest request, Connection connection) throws Exception {
        Stock stock = new StockDAO().findById(request.getStockId(), connection);
        if (stock == null) {
            request.setError("Selected stock not found in the market catalog.");
            return false;
        }
        return true;
    }
}
