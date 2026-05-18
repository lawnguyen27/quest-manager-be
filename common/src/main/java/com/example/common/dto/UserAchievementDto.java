package com.example.common.dto;

import com.example.common.enums.UserAchievementStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementDto {
    private Long id;
    private Long userId;
    private Long achievementId;
    private String achievementName;
    private UserAchievementStatus status;
    private Instant completedAt;
    private BigDecimal points;
    private Boolean pointsAwarded;
}
