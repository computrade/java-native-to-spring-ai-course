package com.computrade.course.spring.ai.tools.service;

import com.computrade.course.spring.ai.tools.model.TaxRequest;
import com.computrade.course.spring.ai.tools.model.TaxResponse;
import org.springframework.stereotype.Service;

// Simulating a closed legacy or third-party service without any Spring AI annotations
@Service
public class LegacyTaxCalculator {

    public TaxResponse calculatePurchaseTax(TaxRequest request) {
        double rate = "US".equalsIgnoreCase(request.getCountry()) ? 0.15 : 0.20;
        return new TaxResponse(
                request.getTicker().toUpperCase(),
                rate,
                "Tax calculated successfully for country: " + request.getCountry()
        );
    }
}
