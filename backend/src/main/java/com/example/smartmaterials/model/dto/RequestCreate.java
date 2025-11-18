package com.example.smartmaterials.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreate {
    @NotNull
    private Long itemId;
    @Min(1)
    private Integer quantity;
    private String purpose;
    private String projectNo;
}
