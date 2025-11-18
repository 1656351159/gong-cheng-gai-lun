package com.example.smartmaterials.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    private Long parentId;
    @NotBlank(message = "name required")
    private String name;
    private Integer safeStock;
    private String unit;
}
