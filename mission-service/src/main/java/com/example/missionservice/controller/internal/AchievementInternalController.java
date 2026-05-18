package com.example.missionservice.controller.internal;

import com.example.common.dto.ApiResponse;
import com.example.common.enums.AchievementType;
import com.example.missionservice.service.UserAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/achievements")
@RequiredArgsConstructor
public class AchievementInternalController {

    private final UserAchievementService userAchievementService;

    @PostMapping("/award")
    public ApiResponse<Void> awardAchievement(
            @RequestParam("userId") Long userId,
            @RequestParam("type") AchievementType type) {
        userAchievementService.awardAchievementInternal(userId, type);
        return ApiResponse.success(null);
    }

    @PostMapping("/init")
    public ApiResponse<Void> initAchievements(@RequestParam("userId") Long userId) {
        userAchievementService.initAchievementsOnLogin(userId);
        return ApiResponse.success(null);
    }
}
