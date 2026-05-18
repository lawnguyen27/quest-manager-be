package com.example.missionservice.controller.privates;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.UserAchievementDto;
import com.example.missionservice.service.UserAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/user-achievements")
@RequiredArgsConstructor
public class UserAchievementPrivateController {

    private final UserAchievementService userAchievementService;

    @GetMapping
    public ApiResponse<Page<UserAchievementDto>> getAllUserAchievements(Pageable pageable) {
        return ApiResponse.success(userAchievementService.getAllUserAchievements(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserAchievementDto> getUserAchievementById(@PathVariable Long id) {
        return ApiResponse.success(userAchievementService.getUserAchievementById(id));
    }
}
