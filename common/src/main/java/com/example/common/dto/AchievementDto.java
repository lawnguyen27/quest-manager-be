package com.example.common.dto;

import com.example.common.enums.AchievementStatus;
import com.example.common.enums.AchievementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDto {
    private Long id;
    private String name;
    private String description;
    private AchievementStatus achievementStatus;
    private BigDecimal points;
    private AchievementType achievementType;
}
