package com.example.missionservice.controller.privates;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.MissionDto;
import com.example.missionservice.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/private/missions")
@RequiredArgsConstructor
public class MissionPrivateController {

    private final MissionService missionService;

    @GetMapping
    public ApiResponse<Page<MissionDto>> getAllMissions(Pageable pageable) {
        return ApiResponse.success(missionService.getAllMissions(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<MissionDto> getMissionById(@PathVariable Long id) {
        return ApiResponse.success(missionService.getMissionById(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<MissionDto> createMission(
            @RequestPart("mission") MissionDto missionDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(missionService.createMission(missionDto, image));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ApiResponse<MissionDto> updateMission(
            @PathVariable Long id,
            @RequestPart("mission") MissionDto missionDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(missionService.updateMission(id, missionDto, image));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ApiResponse.success(null);
    }
}
