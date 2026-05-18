package com.example.missionservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.UserMissionDto;
import com.example.common.dto.UserMissionUpdateDto;
import com.example.common.enums.UserMissionStatus;
import com.example.missionservice.service.UserMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user-missions")
@RequiredArgsConstructor
public class UserMissionPublicController {

    private final UserMissionService userMissionService;

    @GetMapping("/me")
    public ApiResponse<Page<UserMissionDto>> getMyUserMissions(Pageable pageable) {
        return ApiResponse.success(userMissionService.getMyUserMissions(pageable));
    }

    @GetMapping("/me/{id}")
    public ApiResponse<UserMissionDto> getMyUserMissionById(@PathVariable Long id) {
        return ApiResponse.success(userMissionService.getMyUserMissionById(id));
    }

    @PostMapping
    public ApiResponse<UserMissionDto> createUserMission(@RequestBody UserMissionUpdateDto userMissionUpdateDto) {
        return ApiResponse.success(userMissionService.createUserMission(userMissionUpdateDto));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserMissionDto> updateUserMissionStatus(
            @PathVariable Long id,
            @RequestParam UserMissionStatus status) {
        return ApiResponse.success(userMissionService.updateUserMissionStatus(id, status));
    }
}
