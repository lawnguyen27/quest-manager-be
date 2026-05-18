package com.example.missionservice.service;

import com.example.common.dto.UserMissionDto;
import com.example.common.dto.UserMissionUpdateDto;
import com.example.common.enums.UserMissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserMissionService {
    UserMissionDto createUserMission(UserMissionUpdateDto userMissionUpdateDto);
    UserMissionDto updateUserMissionStatus(Long id, UserMissionStatus status);
    Page<UserMissionDto> getAllUserMissions(Pageable pageable);
    UserMissionDto getUserMissionById(Long id);

    Page<UserMissionDto> getMyUserMissions(Pageable pageable);

    UserMissionDto getMyUserMissionById(Long id);
}
