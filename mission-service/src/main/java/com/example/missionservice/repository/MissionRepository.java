package com.example.missionservice.repository;

import com.example.common.enums.MissionStatus;
import com.example.missionservice.entity.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {
    Page<Mission> findAllByMissionStatus(MissionStatus missionStatus, Pageable pageable);
    Optional<Mission> findByMissionStatusAndId(MissionStatus missionStatus, Long id);
}
