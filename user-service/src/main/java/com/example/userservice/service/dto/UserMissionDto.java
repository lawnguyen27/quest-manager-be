package com.example.userservice.service.dto;

import com.example.userservice.entity.enums.UserMissionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMissionDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long missionId;
    private String missionName;
    private UserMissionStatus status;
    private Instant receivedAt;
    private Instant completedAt;
    private BigDecimal points;
}
