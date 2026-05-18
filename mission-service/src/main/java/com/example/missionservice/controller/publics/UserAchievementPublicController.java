package com.example.missionservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.UserAchievementDto;
import com.example.common.dto.UserAchievementUpdateDto;
import com.example.common.enums.UserAchievementStatus;
import com.example.missionservice.service.UserAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user-achievements")
@RequiredArgsConstructor
public class UserAchievementPublicController {

    private final UserAchievementService userAchievementService;

    @GetMapping("/me")
    public ApiResponse<Page<UserAchievementDto>> getMyUserAchievements(Pageable pageable) {
        return ApiResponse.success(userAchievementService.getMyUserAchievements(pageable));
    }

    @GetMapping("/me/{id}")
    public ApiResponse<UserAchievementDto> getMyUserAchievementById(@PathVariable Long id) {
        return ApiResponse.success(userAchievementService.getMyUserAchievementById(id));
    }

    @PostMapping
    public ApiResponse<UserAchievementDto> createUserAchievement(@RequestBody UserAchievementUpdateDto userAchievementUpdateDto) {
        return ApiResponse.success(userAchievementService.createUserAchievement(userAchievementUpdateDto));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserAchievementDto> updateUserAchievementStatus(
            @PathVariable Long id,
            @RequestParam UserAchievementStatus status) {
        return ApiResponse.success(userAchievementService.updateUserAchievementStatus(id, status));
    }
}
