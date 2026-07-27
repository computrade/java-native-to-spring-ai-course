package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.CompanyNews;
import com.computrade.course.spring.ai.tools.model.StockQuote;
import com.computrade.course.spring.ai.tools.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StockMarketSecureToolService {


    private final FinnHubStockMarketService finnHubStockMarketService;
    // 2. The advanced tool used for the Final Lecture (With Secure Context)
    @Tool(
            name = "fetchStockPriceWithContext",
            description = "Retrieves the latest stock price while enforcing corporate tenant authorization restrictions."
    )
    public StockQuote fetchStockPriceWithContext(
            @ToolParam(description = "The uppercase stock exchange ticker symbol, e.g. 'AAPL'.") String symbol,
            ToolContext toolContext
    ) {
        // 1. Extract and convert the raw secure string context into our 3-option Enum
        String rawTenant = (String) toolContext.getContext().get("tenantId");
        Tenant currentTenant = Tenant.fromId(rawTenant);

        // 2. Enforce secure multi-tenancy tier limitations
        if (currentTenant == null || currentTenant == Tenant.GUEST_USER) {
            throw new SecurityException("Access denied. Ticker lookup is blocked for guest users.");
        }

        if (currentTenant == Tenant.COMPUTRADE_STANDARD) {
            // Standard users are only allowed to query blue-chip stocks like AAPL and GOOG
            String upperSymbol = symbol.toUpperCase();
            if (!"AAPL".equals(upperSymbol) && !"GOOG".equals(upperSymbol)) {
                throw new SecurityException("Access denied. Standard tier users are restricted from querying ticker: " + symbol);
            }
        }

        return finnHubStockMarketService.callFinnhubQuoteApi(symbol);
    }


}



