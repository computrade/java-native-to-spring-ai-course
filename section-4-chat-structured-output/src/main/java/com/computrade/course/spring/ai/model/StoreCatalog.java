package com.computrade.course.spring.ai.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class StoreCatalog {

    @NotBlank
    private String storeName;

    @NotBlank
    private String recommendationCategory;

    private List<@Valid ProductInfo> featuredProducts;
    
}
