package com.example.walletservice.service.dto;

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
public class UserItemDto {
    private Long id;
    private Long userId;
    private Long itemId;
    private BigDecimal price;
    private String itemName;
    private Instant purchaseDate;
}
