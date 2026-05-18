package com.example.common.dto;

import com.example.common.enums.UserMissionStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMissionUpdateDto {
    private Long missionId;
    private UserMissionStatus status;
    private BigDecimal points;
}
