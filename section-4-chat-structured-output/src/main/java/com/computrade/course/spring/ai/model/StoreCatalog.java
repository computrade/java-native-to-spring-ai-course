package com.computrade.course.spring.ai.model;

import java.util.List;

public record StoreCatalog(
        String storeName,
        String recommendationCategory,
        List<ProductInfo> featuredProducts // Nested bean
) {}
