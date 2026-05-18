package com.example.missionservice.service.impl;

import com.example.common.api.user.UserClient;
import com.example.common.config.exception.BadRequestException;
import com.example.common.dto.NotificationRequestDto;
import com.example.common.dto.UserAchievementDto;
import com.example.common.dto.UserAchievementUpdateDto;
import com.example.common.event.PointAwardEvent;
import com.example.common.constants.KafkaTopicConstants;
import com.example.common.enums.AchievementType;
import com.example.common.enums.UserAchievementStatus;
import com.example.missionservice.entity.Achievement;
import com.example.missionservice.entity.UserAchievement;
import com.example.missionservice.repository.AchievementRepository;
import com.example.missionservice.repository.UserAchievementRepository;
import com.example.missionservice.service.UserAchievementService;
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

import static com.example.missionservice.constants.MessageCodeConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserClient userClient;

    @Override
    public UserAchievementDto createUserAchievement(UserAchievementUpdateDto userAchievementUpdateDto) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);

        Achievement achievement = achievementRepository.findById(userAchievementUpdateDto.getAchievementId())
                .orElseThrow(() -> new BadRequestException(ACHIEVEMENT_NOT_FOUND));

        if (userAchievementRepository.findByUserIdAndAchievement(currentUserId, achievement).isPresent()) {
            throw new BadRequestException(ACHIEVEMENT_ALREADY_ASSIGNED);
        }

        UserAchievement userAchievement = UserAchievement.builder()
                .userId(currentUserId)
                .achievement(achievement)
                .status(UserAchievementStatus.PROCESSING)
                .points(achievement.getPoints())
                .pointsAwarded(false)
                .build();

        return mapToDto(userAchievementRepository.save(userAchievement));
    }

    @Override
    public UserAchievementDto updateUserAchievementStatus(Long id, UserAchievementStatus status) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);

        UserAchievement userAchievement = userAchievementRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(USER_ACHIEVEMENT_NOT_FOUND));

        if (!currentUserId.equals(userAchievement.getUserId())) {
            throw new BadRequestException(USER_ACHIEVEMENT_ACCESS_DENIED);
        }

        if (UserAchievementStatus.COMPLETED.equals(userAchievement.getStatus())) {
            log.error("UserAchievementId={} already completed", id);
            throw new BadRequestException(USER_ALREADY_COMPLETED_ACHIEVEMENT);
        }

        userAchievement.setStatus(status);

        if (status == UserAchievementStatus.COMPLETED) {
            userAchievement.setCompletedAt(Instant.now());

            if (!Boolean.TRUE.equals(userAchievement.getPointsAwarded())) {
                BigDecimal pointsToAdd = userAchievement.getPoints() != null
                        ? userAchievement.getPoints()
                        : BigDecimal.ZERO;
                try {
                    PointAwardEvent pointAwardEvent = PointAwardEvent.builder()
                            .userId(userAchievement.getUserId())
                            .points(pointsToAdd)
                            .sourceId(String.valueOf(id))
                            .type("ACHIEVEMENT")
                            .build();
                    kafkaTemplate.send(KafkaTopicConstants.ADD_POINTS_TOPIC, pointAwardEvent);
                    log.info("Send kafka add points event: {}", pointAwardEvent);
                    userAchievement.setPointsAwarded(true);
                    log.info("Sent PointAwardEvent for userId={} for userAchievementId={}",
                            userAchievement.getUserId(), id);

                    // Push notification
                    try {
                        userClient.pushNotification(NotificationRequestDto.builder()
                                .userId(userAchievement.getUserId())
                                .title("Achievement Unlocked!")
                                .message("You have unlocked the achievement: " + userAchievement.getAchievement().getName() + " and earned " + pointsToAdd + " points.")
                                .type("ACHIEVEMENT")
                                .build());
                    } catch (Exception e) {
                        log.error("Failed to push notification to user {}: {}", userAchievement.getUserId(), e.getMessage());
                    }
                } catch (Exception e) {
                    log.error("Failed to award points to wallet for userAchievementId={}: {}", id, e.getMessage());
                    throw new BadRequestException(AWARD_POINTS_FAILED);
                }
            }
        }

        return mapToDto(userAchievementRepository.save(userAchievement));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserAchievementDto> getAllUserAchievements(Pageable pageable) {
        return userAchievementRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAchievementDto getUserAchievementById(Long id) {
        return userAchievementRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(USER_ACHIEVEMENT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserAchievementDto> getMyUserAchievements(Pageable pageable) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);
        return userAchievementRepository.findByUserIdOrderByCreatedDateDesc(currentUserId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAchievementDto getMyUserAchievementById(Long id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(userIdStr);
        UserAchievement userAchievement = userAchievementRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(USER_ACHIEVEMENT_NOT_FOUND));
        if (!currentUserId.equals(userAchievement.getUserId())) {
            throw new BadRequestException(USER_ACHIEVEMENT_ACCESS_DENIED);
        }
        return mapToDto(userAchievement);
    }

    @Override
    public void awardAchievementInternal(Long userId, AchievementType type) {
        Achievement achievement = achievementRepository.findByType(type)
                .orElseThrow(() -> new BadRequestException(ACHIEVEMENT_NOT_FOUND));

        if (userAchievementRepository.findByUserIdAndAchievement(userId, achievement).isPresent()) {
            UserAchievement ua = userAchievementRepository.findByUserIdAndAchievement(userId, achievement).get();
            if (ua.getStatus() != UserAchievementStatus.COMPLETED) {
                ua.setStatus(UserAchievementStatus.COMPLETED);
                ua.setCompletedAt(Instant.now());
                userAchievementRepository.save(ua);
                awardPointsAndNotify(userId, ua, achievement);
            }
            return;
        }

        UserAchievement userAchievement = UserAchievement.builder()
                .userId(userId)
                .achievement(achievement)
                .status(UserAchievementStatus.COMPLETED)
                .completedAt(Instant.now())
                .points(achievement.getPoints())
                .pointsAwarded(false)
                .build();

        userAchievement = userAchievementRepository.save(userAchievement);
        awardPointsAndNotify(userId, userAchievement, achievement);
    }

    @Override
    public void initAchievementsOnLogin(Long userId) {
        log.info("Initializing achievements for userId: {}", userId);
        achievementRepository.findAll().forEach(achievement -> {
            log.debug("Checking achievement: {}", achievement.getName());
            if (userAchievementRepository.findByUserIdAndAchievement(userId, achievement).isEmpty()) {
                log.info("Creating UserAchievement for userId: {}, achievement: {}", userId, achievement.getName());
                UserAchievementStatus status = UserAchievementStatus.PROCESSING;
                Instant completedAt = null;

                if (achievement.getType() == AchievementType.LOGIN) {
                    status = UserAchievementStatus.COMPLETED;
                    completedAt = Instant.now();
                }

                UserAchievement userAchievement = UserAchievement.builder()
                        .userId(userId)
                        .achievement(achievement)
                        .status(status)
                        .completedAt(completedAt)
                        .points(achievement.getPoints())
                        .pointsAwarded(false)
                        .build();

                userAchievement = userAchievementRepository.save(userAchievement);

                if (status == UserAchievementStatus.COMPLETED) {
                    awardPointsAndNotify(userId, userAchievement, achievement);
                }
            } else if (achievement.getType() == AchievementType.LOGIN) {
                UserAchievement ua = userAchievementRepository.findByUserIdAndAchievement(userId, achievement).get();
                if (ua.getStatus() != UserAchievementStatus.COMPLETED) {
                    log.info("Completing First Login achievement for userId: {}", userId);
                    ua.setStatus(UserAchievementStatus.COMPLETED);
                    ua.setCompletedAt(Instant.now());
                    userAchievementRepository.save(ua);
                    awardPointsAndNotify(userId, ua, achievement);
                }
            }
        });
    }

    private void awardPointsAndNotify(Long userId, UserAchievement userAchievement, Achievement achievement) {
        try {
            PointAwardEvent pointAwardEvent = PointAwardEvent.builder()
                    .userId(userId)
                    .points(userAchievement.getPoints())
                    .sourceId(String.valueOf(userAchievement.getId()))
                    .type("ACHIEVEMENT")
                    .build();
            kafkaTemplate.send(KafkaTopicConstants.ADD_POINTS_TOPIC, pointAwardEvent);
            userAchievement.setPointsAwarded(true);
            userAchievementRepository.save(userAchievement);

            userClient.pushNotification(NotificationRequestDto.builder()
                    .userId(userId)
                    .title("Achievement Unlocked!")
                    .message("You have unlocked the achievement: " + achievement.getName())
                    .type("ACHIEVEMENT")
                    .build());
        } catch (Exception e) {
            log.error("Failed to process point award for achievement: {}", achievement.getName(), e);
        }
    }

    private UserAchievementDto mapToDto(UserAchievement userAchievement) {
        Achievement achievement = userAchievement.getAchievement();
        return UserAchievementDto.builder()
                .id(userAchievement.getId())
                .userId(userAchievement.getUserId())
                .achievementId(achievement.getId())
                .achievementName(achievement.getName())
                .status(userAchievement.getStatus())
                .completedAt(userAchievement.getCompletedAt())
                .points(userAchievement.getPoints())
                .pointsAwarded(userAchievement.getPointsAwarded())
                .build();
    }
}
