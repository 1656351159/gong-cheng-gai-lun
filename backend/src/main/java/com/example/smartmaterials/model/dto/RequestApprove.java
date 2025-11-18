package com.example.smartmaterials.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestApprove {
    @NotBlank
    private String comment;
}
