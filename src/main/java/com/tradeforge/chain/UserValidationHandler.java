package com.tradeforge.chain;

import com.tradeforge.dao.UserDAO;
import com.tradeforge.model.User;
import java.sql.Connection;

/**
 * Concrete Validation Handler: Verifies user exists and account status is ACTIVE.
 */
public class UserValidationHandler extends ValidationHandler {

    @Override
    protected boolean check(TradingRequest request, Connection connection) throws Exception {
        User user = new UserDAO().findById(request.getUserId(), connection);
        if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            request.setError("User account is invalid or not active.");
            return false;
        }
        return true;
    }
}
