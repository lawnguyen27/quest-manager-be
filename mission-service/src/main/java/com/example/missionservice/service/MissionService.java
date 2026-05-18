package com.example.missionservice.service;

import com.example.common.dto.MissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.multipart.MultipartFile;

public interface MissionService {
    Page<MissionDto> getAllMissions(Pageable pageable);
    Page<MissionDto> getAllMissionsByUser(Pageable pageable);
    MissionDto getMissionById(Long id);
    MissionDto getMissionByIdByUser(Long id);
    MissionDto createMission(MissionDto missionDto, MultipartFile image);
    MissionDto updateMission(Long id, MissionDto missionDto, MultipartFile image);
    void deleteMission(Long id);
}
