package com.tradeforge.chain;

import com.tradeforge.dao.StockDAO;
import com.tradeforge.dao.UserDAO;
import com.tradeforge.factory.TransactionFactory;
import com.tradeforge.model.Stock;
import com.tradeforge.model.User;
import java.sql.Connection;

/**
 * Concrete Validation Handler: Verifies user has sufficient balance for BUY orders.
 */
public class BalanceValidationHandler extends ValidationHandler {

    @Override
    protected boolean check(TradingRequest request, Connection connection) throws Exception {
        if (!"BUY".equalsIgnoreCase(request.getType())) {
            return true; // Balance check only applies to BUY orders
        }

        User user = new UserDAO().findById(request.getUserId(), connection);
        Stock stock = new StockDAO().findById(request.getStockId(), connection);

        if (user == null || stock == null) {
            request.setError("Cannot verify balance: user or stock record missing.");
            return false;
        }

        double totalRequired = TransactionFactory.create("BUY").calculateTotal(stock.getPrice(), request.getQuantity());
        if (user.getBalance() < totalRequired) {
            request.setError(String.format("Insufficient account balance. Required: ₹%.2f, Available: ₹%.2f",
                    totalRequired, user.getBalance()));
            return false;
        }
        return true;
    }
}
