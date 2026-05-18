package com.example.walletservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TopUpRequestDto {
    @NotBlank(message = "Package code is required")
    private String packageCode;
}
