package com.example.missionservice.repository;

import com.example.missionservice.entity.Mission;
import com.example.missionservice.entity.UserMission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long>, JpaSpecificationExecutor<UserMission> {
    Optional<UserMission> findByUserIdAndMission(Long userId, Mission mission);

    Page<UserMission> findByUserIdOrderByReceivedAtDesc(Long userId, Pageable pageable);
}
