package com.tradeforge.proxy;

import com.tradeforge.chain.TradingRequest;
import com.tradeforge.template.TradeResult;

/**
 * DESIGN PATTERN 3: PROXY PATTERN (Protection Proxy)
 * 
 * Responsibility:
 * Controls access to RealTradingService.
 * Enforces security validations:
 * 1. Verifies the user is actively logged in (authenticatedUserId > 0).
 * 2. Verifies the trade request belongs to the authenticated user (prevents session hijacking).
 * 3. Validates the request object is non-null.
 * 
 * Only delegates to RealTradingService when all security checks pass.
 */
public class TradingServiceProxy implements TradingService {

    private final TradingService realService;

    public TradingServiceProxy(TradingService realService) {
        this.realService = realService;
    }

    @Override
    public TradeResult executeTrade(TradingRequest request, int authenticatedUserId) throws Exception {
        // Security check 1: Must be authenticated
        if (authenticatedUserId <= 0) {
            throw new SecurityException("Access Denied: You must be logged in to execute trades.");
        }

        // Security check 2: Request must not be null
        if (request == null) {
            throw new IllegalArgumentException("Trade request cannot be null.");
        }

        // Security check 3: User ID in request must match authenticated user ID
        if (request.getUserId() != authenticatedUserId) {
            throw new SecurityException("Access Denied: Cannot execute trade for another user account.");
        }

        // Security checks passed: Delegate to the real service
        return realService.executeTrade(request, authenticatedUserId);
    }
}
