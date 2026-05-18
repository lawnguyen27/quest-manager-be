package com.example.missionservice.repository;

import com.example.common.enums.AchievementStatus;
import com.example.common.enums.AchievementType;
import com.example.missionservice.entity.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long>, JpaSpecificationExecutor<Achievement> {
    Page<Achievement> findAllByAchievementStatus(AchievementStatus achievementStatus, Pageable pageable);
    Optional<Achievement> findByAchievementStatusAndId(AchievementStatus achievementStatus, Long id);
    Optional<Achievement> findByType(AchievementType type);
}
