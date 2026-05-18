package com.example.missionservice.service.impl;

import com.example.common.config.exception.BadRequestException;
import com.example.common.dto.AchievementDto;
import com.example.common.enums.AchievementStatus;
import com.example.missionservice.entity.Achievement;
import com.example.missionservice.repository.AchievementRepository;
import com.example.missionservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.example.missionservice.constants.MessageCodeConstants.ACHIEVEMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public Page<AchievementDto> getAllAchievement(Pageable pageable) {
        return achievementRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public Page<AchievementDto> getAllAchievementByUser(Pageable pageable) {
        return achievementRepository.findAllByAchievementStatus(AchievementStatus.ACTIVE, pageable).map(this::mapToDto);
    }

    @Override
    public AchievementDto getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(ACHIEVEMENT_NOT_FOUND));
    }

    @Override
    public AchievementDto getAchievementByIdByUser(Long id) {
        return achievementRepository.findByAchievementStatusAndId(AchievementStatus.ACTIVE, id)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(ACHIEVEMENT_NOT_FOUND));    }

    @Override
    public AchievementDto createAchievement(AchievementDto achievementDto) {
        Achievement achievement = new Achievement();
        updateEntity(achievement, achievementDto);
        return mapToDto(achievementRepository.save(achievement));
    }

    @Override
    public AchievementDto updateAchievement(Long id, AchievementDto achievementDto) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ACHIEVEMENT_NOT_FOUND));
        updateEntity(achievement, achievementDto);
        return mapToDto(achievementRepository.save(achievement));
    }

    @Override
    public void deleteAchievement(Long id) {
        Achievement achievement = achievementRepository.findById(id).orElse(null);
        if (Objects.isNull(achievement)) {
            throw new BadRequestException(ACHIEVEMENT_NOT_FOUND);
        }
        achievement.setAchievementStatus(AchievementStatus.INACTIVE);
        achievementRepository.save(achievement);
    }

    private AchievementDto mapToDto(Achievement achievement) {
        return AchievementDto.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .achievementType(achievement.getType())
                .achievementStatus(achievement.getAchievementStatus())
                .points(achievement.getPoints())
                .build();
    }

    private void updateEntity(Achievement achievement, AchievementDto dto) {
        achievement.setName(dto.getName());
        achievement.setDescription(dto.getDescription());
        achievement.setType(dto.getAchievementType());
        achievement.setAchievementStatus(dto.getAchievementStatus());
        achievement.setPoints(dto.getPoints());
    }
}
