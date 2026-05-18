package com.example.common.dto;

import com.example.common.enums.UserAchievementStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementUpdateDto {
    private Long achievementId;
    private UserAchievementStatus status;
    private BigDecimal points;
}
