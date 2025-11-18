package com.example.smartmaterials.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MaterialRequest {
    @NotNull
    private Long categoryId;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    private String spec;
    @PositiveOrZero
    private BigDecimal unitPrice;
    private String currency;
}
