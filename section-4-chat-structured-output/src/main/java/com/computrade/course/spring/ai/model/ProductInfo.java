package com.computrade.course.spring.ai.model;


import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductInfo {

        @NotBlank
        @Size(min = 2, max = 100)
        @JsonPropertyDescription("The full official commercial name of the product, including the specific model number if applicable.")
        private String productName;

        @NotBlank
        @JsonPropertyDescription("The primary company or brand that manufactures the product (e.g., Apple, Sony, Samsung).")
        private String manufacturer;

        @Min(0)
        @Max(1000)
        @JsonPropertyDescription("The current estimated retail price in USD. Do not include currency symbols, provide only the floating-point number.")
        private Double priceUSD;

        private ProductStatus status;

}
