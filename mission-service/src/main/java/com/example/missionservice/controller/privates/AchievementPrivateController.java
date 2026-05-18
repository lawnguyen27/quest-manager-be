package com.example.missionservice.controller.privates;

import com.example.common.dto.AchievementDto;
import com.example.common.dto.ApiResponse;
import com.example.missionservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private/achievements")
@RequiredArgsConstructor
public class AchievementPrivateController {

    private final AchievementService achievementService;

    @GetMapping
    public ApiResponse<Page<AchievementDto>> getAllAchievements(Pageable pageable) {
        return ApiResponse.success(achievementService.getAllAchievement(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<AchievementDto> getAchievementById(@PathVariable Long id) {
        return ApiResponse.success(achievementService.getAchievementById(id));
    }

    @PostMapping
    public ApiResponse<AchievementDto> createAchievement(@RequestBody AchievementDto achievementDto) {
        return ApiResponse.success(achievementService.createAchievement(achievementDto));
    }

    @PutMapping("/{id}")
    public ApiResponse<AchievementDto> updateAchievement(@PathVariable Long id, @RequestBody AchievementDto achievementDto) {
        return ApiResponse.success(achievementService.updateAchievement(id, achievementDto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ApiResponse.success(null);
    }
}
