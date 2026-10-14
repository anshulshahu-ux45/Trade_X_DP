package com.tradeforge.proxy;

import com.tradeforge.chain.TradingRequest;
import com.tradeforge.template.TradeResult;

/**
 * DESIGN PATTERN 3: PROXY PATTERN (Subject Interface)
 * 
 * Defines the contract for executing trades. Both the RealTradingService
 * and the TradingServiceProxy implement this interface.
 */
public interface TradingService {

    /**
     * Executes a trading request for an authenticated user.
     * 
     * @param request the trade request to execute
     * @param authenticatedUserId the currently authenticated user's ID
     * @return TradeResult detailing outcome and assigned agent
     * @throws Exception if validation or execution fails
     */
    TradeResult executeTrade(TradingRequest request, int authenticatedUserId) throws Exception;
}
