package com.example.missionservice.controller.publics;

import com.example.common.dto.AchievementDto;
import com.example.common.dto.ApiResponse;
import com.example.missionservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/achievements")
@RequiredArgsConstructor
public class AchievementPublicController {

    private final AchievementService achievementService;

    @GetMapping
    public ApiResponse<Page<AchievementDto>> getAllAchievements(Pageable pageable) {
        return ApiResponse.success(achievementService.getAllAchievementByUser(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<AchievementDto> getAchievementById(@PathVariable Long id) {
        return ApiResponse.success(achievementService.getAchievementByIdByUser(id));
    }
}
