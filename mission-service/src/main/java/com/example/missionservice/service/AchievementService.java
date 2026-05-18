package com.example.missionservice.service;

import com.example.common.dto.AchievementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AchievementService {
    Page<AchievementDto> getAllAchievement(Pageable pageable);
    Page<AchievementDto> getAllAchievementByUser(Pageable pageable);
    AchievementDto getAchievementById(Long id);
    AchievementDto getAchievementByIdByUser(Long id);
    AchievementDto createAchievement(AchievementDto achievementDto);
    AchievementDto updateAchievement(Long id, AchievementDto achievementDto);
    void deleteAchievement(Long id);
}
