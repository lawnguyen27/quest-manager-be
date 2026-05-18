package com.example.missionservice.repository;

import com.example.missionservice.entity.Achievement;
import com.example.missionservice.entity.UserAchievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long>, JpaSpecificationExecutor<UserAchievement> {
    Optional<UserAchievement> findByUserIdAndAchievement(Long userId, Achievement achievement);
    Page<UserAchievement> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);
}
