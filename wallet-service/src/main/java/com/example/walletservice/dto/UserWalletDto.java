package com.example.walletservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletDto {
    private Long id;
    private Long userId;
    private BigDecimal points;
    private Instant createdAt;
    private Instant updatedAt;
}
