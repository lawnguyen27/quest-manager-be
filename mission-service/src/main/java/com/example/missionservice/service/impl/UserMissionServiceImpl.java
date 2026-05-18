package com.example.missionservice.service.impl;

import com.example.common.api.user.UserClient;
import com.example.common.dto.NotificationRequestDto;
import com.example.common.dto.UserMissionDto;
import com.example.common.dto.UserMissionUpdateDto;
import com.example.common.event.PointAwardEvent;
import com.example.common.constants.KafkaTopicConstants;
import com.example.common.enums.AchievementType;
import com.example.common.enums.UserMissionStatus;
import com.example.common.config.exception.BadRequestException;
import com.example.missionservice.entity.Mission;
import com.example.missionservice.entity.UserMission;
import com.example.missionservice.repository.MissionRepository;
import com.example.missionservice.repository.UserMissionRepository;
import com.example.missionservice.service.UserAchievementService;
import com.example.missionservice.service.UserMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import static com.example.missionservice.constants.MessageCodeConstants.AWARD_POINTS_FAILED;
import static com.example.missionservice.constants.MessageCodeConstants.MISSION_ALREADY_ASSIGNED;
import static com.example.missionservice.constants.MessageCodeConstants.MISSION_NOT_FOUND;
import static com.example.missionservice.constants.MessageCodeConstants.USER_ALREADY_COMPLETED_MISSION;
import static com.example.missionservice.constants.MessageCodeConstants.USER_MISSION_ACCESS_DENIED;
import static com.example.missionservice.constants.MessageCodeConstants.USER_MISSION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserMissionServiceImpl implements UserMissionService {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserClient userClient;
    private final UserAchievementService userAchievementService;

    @Override
    public UserMissionDto createUserMission(UserMissionUpdateDto userMissionUpdateDto) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);

        Mission mission = missionRepository.findById(userMissionUpdateDto.getMissionId())
                .orElseThrow(() -> new BadRequestException(MISSION_NOT_FOUND));

        if (userMissionRepository.findByUserIdAndMission(currentUserId, mission).isPresent()) {
            throw new BadRequestException(MISSION_ALREADY_ASSIGNED);
        }

        UserMission userMission = UserMission.builder()
                .userId(currentUserId)
                .mission(mission)
                .status(UserMissionStatus.PROCESSING)
                .receivedAt(Instant.now())
                .points(mission.getPoints())
                .pointsAwarded(false)
                .build();

        return mapToDto(userMissionRepository.save(userMission));
    }

    @Override
    public UserMissionDto updateUserMissionStatus(Long id, UserMissionStatus status) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);

        UserMission userMission = userMissionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(USER_MISSION_NOT_FOUND));

        if (!currentUserId.equals(userMission.getUserId())) {
            throw new BadRequestException(USER_MISSION_ACCESS_DENIED);
        }

        if (UserMissionStatus.COMPLETED.equals(userMission.getStatus())) {
            log.error("UserMissionId={} already completed", id);
            throw new BadRequestException(USER_ALREADY_COMPLETED_MISSION);
        }

        userMission.setStatus(status);

            if (status == UserMissionStatus.COMPLETED) {
            userMission.setCompletedAt(Instant.now());

            if (!Boolean.TRUE.equals(userMission.getPointsAwarded())) {
                BigDecimal pointsToAdd = userMission.getPoints() != null
                        ? userMission.getPoints()
                        : BigDecimal.ZERO;
                try {
                    PointAwardEvent pointAwardEvent = PointAwardEvent.builder()
                            .userId(userMission.getUserId())
                            .points(pointsToAdd)
                            .sourceId(String.valueOf(id))
                            .type("MISSION")
                            .build();
                    kafkaTemplate.send(KafkaTopicConstants.ADD_POINTS_TOPIC, pointAwardEvent);
                    log.info("Send kafka add points event: {}", pointAwardEvent);
                    userMission.setPointsAwarded(true);
                    log.info("Sent PointAwardEvent for userId={} for userMissionId={}",
                            userMission.getUserId(), id);
                            
                    // Push notification
                    try {
                        userClient.pushNotification(NotificationRequestDto.builder()
                                .userId(userMission.getUserId())
                                .title("Mission Completed!")
                                .message("You have completed the mission: " + userMission.getMission().getName() + " and earned " + pointsToAdd + " points.")
                                .type("MISSION")
                                .build());
                    } catch (Exception e) {
                        log.error("Failed to push notification to user {}: {}", userMission.getUserId(), e.getMessage());
                    }

                    // Award First Mission Achievement
                    try {
                        userAchievementService.awardAchievementInternal(userMission.getUserId(), AchievementType.MISSION);
                    } catch (Exception e) {
                        log.error("Failed to award mission achievement", e);
                    }
                } catch (Exception e) {
                    log.error("Failed to award points to wallet for userMissionId={}: {}", id, e.getMessage());
                    throw new BadRequestException(AWARD_POINTS_FAILED);
                }
            }
        }

        return mapToDto(userMissionRepository.save(userMission));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserMissionDto> getAllUserMissions(Pageable pageable) {
        return userMissionRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMissionDto getUserMissionById(Long id) {
        return userMissionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(USER_MISSION_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserMissionDto> getMyUserMissions(Pageable pageable) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);
        return userMissionRepository.findByUserIdOrderByReceivedAtDesc(currentUserId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMissionDto getMyUserMissionById(Long id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);
        UserMission userMission = userMissionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(USER_MISSION_NOT_FOUND));
        if (!currentUserId.equals(userMission.getUserId())) {
            throw new BadRequestException(USER_MISSION_ACCESS_DENIED);
        }
        return mapToDto(userMission);
    }

    private UserMissionDto mapToDto(UserMission userMission) {
        Mission mission = userMission.getMission();
        return UserMissionDto.builder()
                .id(userMission.getId())
                .userId(userMission.getUserId())
                .userEmail("User ID: " + userMission.getUserId())
                .missionId(mission.getId())
                .missionName(mission.getName())
                .videoUrl(mission.getVideoUrl())
                .status(userMission.getStatus())
                .receivedAt(userMission.getReceivedAt())
                .completedAt(userMission.getCompletedAt())
                .points(userMission.getPoints())
                .pointsAwarded(userMission.getPointsAwarded())
                .build();
    }
}
