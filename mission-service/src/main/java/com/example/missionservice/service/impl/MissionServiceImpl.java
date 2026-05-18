package com.example.missionservice.service.impl;

import com.example.common.config.exception.BadRequestException;
import com.example.common.dto.MissionDto;
import com.example.common.enums.MissionStatus;
import com.example.missionservice.entity.Mission;
import com.example.missionservice.repository.MissionRepository;
import com.example.common.service.FileUploadService;
import com.example.missionservice.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.example.missionservice.constants.MessageCodeConstants.MISSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional(readOnly = true)
    public Page<MissionDto> getAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MissionDto> getAllMissionsByUser(Pageable pageable) {
        return missionRepository.findAllByMissionStatus(MissionStatus.ACTIVE, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MissionDto getMissionById(Long id) {
        return missionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(MISSION_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public MissionDto getMissionByIdByUser(Long id) {
        return missionRepository.findByMissionStatusAndId(MissionStatus.ACTIVE, id)
                .map(this::mapToDto)
                .orElseThrow(() -> new BadRequestException(MISSION_NOT_FOUND));
    }

    @Override
    public MissionDto createMission(MissionDto missionDto, MultipartFile image) {
        Mission mission = new Mission();
        applyDto(mission, missionDto);
        if (image != null && !image.isEmpty()) {
            mission.setImageLink(fileUploadService.uploadFile(image));
        }
        return mapToDto(missionRepository.save(mission));
    }

    @Override
    public MissionDto updateMission(Long id, MissionDto missionDto, MultipartFile image) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(MISSION_NOT_FOUND));
        applyDto(mission, missionDto);
        if (image != null && !image.isEmpty()) {
            mission.setImageLink(fileUploadService.uploadFile(image));
        }
        return mapToDto(missionRepository.save(mission));
    }

    @Override
    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new BadRequestException(MISSION_NOT_FOUND);
        }
        missionRepository.deleteById(id);
    }

    private MissionDto mapToDto(Mission mission) {
        return MissionDto.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .missionType(mission.getMissionType())
                .documentLink(mission.getDocumentLink())
                .imageLink(mission.getImageLink())
                .videoUrl(mission.getVideoUrl())
                .fromDate(mission.getFromDate())
                .toDate(mission.getToDate())
                .missionStatus(mission.getMissionStatus())
                .points(mission.getPoints())
                .build();
    }

    /**
     * Applies DTO fields without wiping optional URLs/dates when the client omits them (multipart JSON).
     * Non-blank {@code imageLink} in the DTO still updates the stored link when no new file is uploaded.
     */
    private void applyDto(Mission mission, MissionDto dto) {
        mission.setName(dto.getName());
        mission.setDescription(dto.getDescription());
        mission.setMissionType(dto.getMissionType());
        mission.setMissionStatus(dto.getMissionStatus());
        mission.setPoints(dto.getPoints());
        if (dto.getFromDate() != null) {
            mission.setFromDate(dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            mission.setToDate(dto.getToDate());
        }
        if (StringUtils.hasText(dto.getDocumentLink())) {
            mission.setDocumentLink(dto.getDocumentLink());
        }
        if (StringUtils.hasText(dto.getVideoUrl())) {
            mission.setVideoUrl(dto.getVideoUrl());
        }
        if (StringUtils.hasText(dto.getImageLink())) {
            mission.setImageLink(dto.getImageLink());
        }
    }
}
