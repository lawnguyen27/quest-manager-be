package com.example.common.dto;

import com.example.common.enums.MissionStatus;
import com.example.common.enums.MissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionDto {
    private Long id;
    private String name;
    private String description;
    private MissionType missionType;
    private String documentLink;
    private String imageLink;
    private String videoUrl;
    private Instant fromDate;
    private Instant toDate;
    private MissionStatus missionStatus;
    private BigDecimal points;
}
