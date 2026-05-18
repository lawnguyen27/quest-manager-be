package com.example.walletservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageLink;
    /** Null or true = available; explicit false = hidden / soft-off catalog */
    private Boolean isAvailable;
}
