package com.example.userservice.service.dto;

import com.example.userservice.entity.enums.UserMissionStatus;
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
