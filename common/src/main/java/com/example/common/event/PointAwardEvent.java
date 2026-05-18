package com.example.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointAwardEvent {
    private Long userId;
    private BigDecimal points;
    private String sourceId;
    private String type; // MISSION, ACHIEVEMENT
}
