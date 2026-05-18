package com.example.missionservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.MissionDto;
import com.example.missionservice.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/missions")
@RequiredArgsConstructor
public class MissionPublicController {

    private final MissionService missionService;

    @GetMapping
    public ApiResponse<Page<MissionDto>> getAllMissions(Pageable pageable) {
        return ApiResponse.success(missionService.getAllMissionsByUser(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<MissionDto> getMissionById(@PathVariable Long id) {
        return ApiResponse.success(missionService.getMissionByIdByUser(id));
    }
}
