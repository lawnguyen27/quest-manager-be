package com.example.missionservice.service;

import com.example.common.dto.UserAchievementDto;
import com.example.common.dto.UserAchievementUpdateDto;
import com.example.common.enums.AchievementType;
import com.example.common.enums.UserAchievementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAchievementService {
    UserAchievementDto createUserAchievement(UserAchievementUpdateDto userAchievementUpdateDto);
    UserAchievementDto updateUserAchievementStatus(Long id, UserAchievementStatus status);
    Page<UserAchievementDto> getAllUserAchievements(Pageable pageable);
    UserAchievementDto getUserAchievementById(Long id);
    Page<UserAchievementDto> getMyUserAchievements(Pageable pageable);
    UserAchievementDto getMyUserAchievementById(Long id);
    void awardAchievementInternal(Long userId, AchievementType type);
    void initAchievementsOnLogin(Long userId);
}
