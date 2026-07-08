package com.computrade.course.spring.ai.model;

import java.util.List;

public record CarDetails(
        String brand,
        String model,
        int horsepower,
        List<String> keyFeatures,
        double estimatedPriceUSD
) {}
